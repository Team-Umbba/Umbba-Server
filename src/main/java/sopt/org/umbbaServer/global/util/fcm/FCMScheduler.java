package sopt.org.umbbaServer.global.util.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
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
@Transactional
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

        parentchildList.stream()
            .forEach(pc -> {
                log.info(pc.getId() + "번째 Parentchild");
//                String cronExpression = String.format("0 %s %s * * ?", pc.getPushTime().getMinute(), pc.getPushTime().getHour());
                String cronExpression = String.format("*/30 * * * * *");
                log.info("cron: {}", cronExpression);
                fcmService.schedulePushAlarm(cronExpression, pc);
            });
        return "다수 기기 알림 전송 성공 ! messages were sent successfully";
    }
}
