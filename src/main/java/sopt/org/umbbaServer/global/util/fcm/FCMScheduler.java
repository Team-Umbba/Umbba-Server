package sopt.org.umbbaServer.global.util.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMPushRequestDto;

@Slf4j
@Component
@RestController
@EnableScheduling
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
                String cronExpression = String.format("0 %s %s * * ?", pc.getPushTime().getMinute(), pc.getPushTime().getHour());
//                String cronExpression = String.format("*/10 * * * * *");
                log.info("cron: {}", cronExpression);
                fcmService.schedulePushAlarm(cronExpression, pc.getId());
            });
        return "Today QnA messages were sent successfully";
    }

    @Scheduled(cron = "0 15 4 * * ?", zone = "Asia/Seoul")
    public String drink() {
        fcmService.multipleSendByToken(FCMPushRequestDto.sendTodayQna("술이슈", "새벽4시 술 먹을시간"), 3L);

        return "Today QnA messages were sent successfully";
    }
}
