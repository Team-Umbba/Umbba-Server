package sopt.org.umbbaServer.global.util.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMPushRequestDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleTask {

    private final TaskScheduler taskScheduler;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    public void schedulePushAlarm(String cronExpression, Parentchild parentchild) {

        taskScheduler.schedule(() -> {

            log.info("성립된 부모자식- 초대코드: {}, 인덱스: {}", parentchild.getInviteCode(), parentchild.getCount());

            if (!parentchild.getQnaList().isEmpty()) {

                QnA currentQnA = parentchild.getQnaList().get(parentchild.getCount() - 1);
                if (currentQnA.isParentAnswer() && currentQnA.isChildAnswer()) {

                    log.info("둘 다 답변함 다음 질문으로 ㄱ {}", parentchild.getCount());
                    parentchild.addCount();
                    log.info("스케줄링 작업 예약 내 addCount 후 count: {}", parentchild.getCount());

                    QnA todayQnA = parentchild.getQnaList().get(parentchild.getCount() - 1);

                    List<User> parentChildUsers = userRepository.findUserByParentChild(parentchild);

                    log.info("FCMService - schedulePushAlarm() 실행");
                    parentChildUsers.stream()
                            .filter(user -> user.validateParentchild(parentChildUsers) && !user.getSocialPlatform().equals(SocialPlatform.WITHDRAW))
                            .forEach(user -> {
                                log.info("FCMService-schedulePushAlarm() topic: {}", todayQnA.getQuestion().getTopic());
                                fcmService.multipleSendByToken(FCMPushRequestDto.sendTodayQna(todayQnA.getQuestion().getSection().getValue(), todayQnA.getQuestion().getTopic()), parentchild.getId());
                            });

                    if (todayQnA == null) {
                        log.error("{}번째 Parentchild의 QnAList가 존재하지 않음!", parentchild.getId());
                    }
                }
            }

        }, new CronTrigger(cronExpression));
    }
}
