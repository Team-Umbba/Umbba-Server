package sopt.org.umbba.global.util.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sopt.org.umbba.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbba.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbba.domain.user.model.User;
import sopt.org.umbba.domain.user.repository.UserRepository;
import sopt.org.umbba.domain.user.social.SocialPlatform;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMScheduler {

    private final ParentchildRepository parentchildRepository;
    private final UserRepository userRepository;
    private final ParentchildDao parentchildDao;
    private final FCMService fcmService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")  // 초기값
    public String pushTodayQna()  {

        log.info("오늘의 질문 알람 - 유저마다 보내는 시간 다름");
//        List<String> tokenList = parentchildDao.findFcmTokensById(parentchildId);


        parentchildRepository.findAll().stream()
                .filter(pc -> {
                    List<String> tokenList = parentchildDao.findFcmTokensById(pc.getId());
                    List<User> parentChildUsers = userRepository.findUserByParentChild(pc);
                    return tokenList != null &&
                            tokenList.size() == 2 &&
                            parentChildUsers.stream()
                                    .allMatch(user -> user.validateParentchild(parentChildUsers) && !user.getSocialPlatform().equals(SocialPlatform.WITHDRAW));
                })
                .forEach(pc -> {
                log.info(pc.getId() + "번째 Parentchild");
                String cronExpression = String.format("0 %s %s * * ?", pc.getPushTime().getMinute(), pc.getPushTime().getHour());
//                String cronExpression = String.format("*/20 * * * * *");
                log.info("cron: {}", cronExpression);
                fcmService.schedulePushAlarm(cronExpression, pc.getId());
            })
                ;
        return "Today QnA messages were sent successfully";
    }

//    @Scheduled(cron = "0 0 4 * * ?", zone = "Asia/Seoul")
//    public String drink() {
//        fcmService.multipleSendByToken(FCMPushRequestDto.sendTodayQna("술이슈", "새벽4시 술 먹을시간"), 3L);
//
//        return "Today QnA messages were sent successfully";
//    }
}
