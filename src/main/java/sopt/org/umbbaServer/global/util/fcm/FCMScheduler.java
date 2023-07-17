package sopt.org.umbbaServer.global.util.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbbaServer.domain.qna.dao.QnADao;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMScheduler {

    private final UserRepository userRepository;
    private final ParentchildRepository parentchildRepository;
    private final QnADao qnADao;

    private final FCMService fcmService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")  // 초기값
    public String pushTodayQna() {

        log.info("오늘의 질문 알람 - 유저마다 보내는 시간 다름");
        List<Parentchild> parentchildList = parentchildRepository.findAll();
        /*for (Parentchild pcd : parentchildList) {
            List<User> parentChildUsers = userRepository.findUserByParentChild(pcd);
            if (!parentChildUsers.get(0).validateParentchild(parentChildUsers)) {
                parentchildList.remove(pcd);
            }

            for (User user : parentChildUsers) {
                if (user.getSocialPlatform().equals(SocialPlatform.WITHDRAW)) {
                    parentchildList.remove(pcd);
                }
            }
        }*/

        parentchildList = parentchildList.stream()
                .filter(pc -> {
                    List<User> parentChildUsers = userRepository.findUserByParentChild(pc);
                    return parentChildUsers.get(0).validateParentchild(parentChildUsers) &&
                            parentChildUsers.stream()
                                    .noneMatch(user -> user.getSocialPlatform().equals(SocialPlatform.WITHDRAW));
                })
                .collect(Collectors.toList());

        parentchildList.stream()
            .forEach(pc -> {
                log.info(pc.getId() + "번째 Parentchild");
//                    String cronExpression = String.format("0 %s %s * * ?", pc.getPushTime().getMinute(), pc.getPushTime().getHour());
                String cronExpression = String.format("*/10 * * * * *");
                log.info("cron: {}", cronExpression);
                /*QnA todayQnA = qnADao.findQuestionByParentchildId(pc.getId()).orElseThrow(
                        () -> new CustomException(ErrorType.PARENTCHILD_HAVE_NO_QNALIST)
                );*/
                Parentchild parentchild = parentchildRepository.findById(pc.getId()).get();
                QnA todayQnA = parentchild.getQnaList().get(parentchild.getCount()-1);

                /*Optional<QnA> todayQnA = qnADao.findQuestionByParentchildId(pc.getId());
                todayQnA.ifPresent(qna -> {
                    log.info("todayQnA: {}", qna.getQuestion().getTopic());

                    fcmService.schedulePushAlarm(cronExpression, qna.getQuestion(), pc.getId());  // cron 스케줄을 이용해 작업 예약
                });*/

                if (todayQnA.isParentAnswer() && todayQnA.isChildAnswer()) {
                    // 다음날 질문으로 넘어감
                    parentchild.addCount();
                    fcmService.schedulePushAlarm(cronExpression, todayQnA.getQuestion(), pc.getId());;
                }
                if (todayQnA == null) {
                    log.error("{}번째 Parentchild의 QnAList가 존재하지 않음!", pc.getId());
                }
            });
        return "다수 기기 알림 전송 성공 ! messages were sent successfully";
    }
}
