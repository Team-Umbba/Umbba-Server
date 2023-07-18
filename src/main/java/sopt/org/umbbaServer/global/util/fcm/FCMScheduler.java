package sopt.org.umbbaServer.global.util.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;

@Slf4j
@Component
//@Transactional
@RestController
@RequiredArgsConstructor
public class FCMScheduler {

    private final ParentchildRepository parentchildRepository;
    private final FCMService fcmService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")  // 초기값
    public String pushTodayQna()  {

        log.info("오늘의 질문 알람 - 유저마다 보내는 시간 다름");

        parentchildRepository.findAll().stream()
            .forEach(pc -> {
                log.info(pc.getId() + "번째 Parentchild");
//                String cronExpression = String.format("0 %s %s * * ?", pc.getPushTime().getMinute(), pc.getPushTime().getHour());
                String cronExpression = String.format("*/10 * * * * *");
                log.info("cron: {}", cronExpression);
                log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!pc.getId() - {}", pc.getId());
                fcmService.schedulePushAlarm(cronExpression, pc.getId());
            });
        return "Today QnA messages were sent successfully";
    }
}
