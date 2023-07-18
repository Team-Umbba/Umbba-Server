package sopt.org.umbbaServer.global.util.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbbaServer.domain.qna.dao.QnADao;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;

import java.util.List;

@EnableScheduling
@Slf4j
@Component
//@Transactional
@RestController
@RequiredArgsConstructor
public class FCMScheduler {

    private final ParentchildRepository parentchildRepository;
    private final ScheduleTask scheduleTask;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")  // 초기값
    public String pushTodayQna() throws InterruptedException {

        log.info("오늘의 질문 알람 - 유저마다 보내는 시간 다름");
        Thread.sleep(1000);

        parentchildRepository.findAll().stream()
            .forEach(pc -> {
                log.info(pc.getId() + "번째 Parentchild");
//                String cronExpression = String.format("0 %s %s * * ?", pc.getPushTime().getMinute(), pc.getPushTime().getHour());
                String cronExpression = String.format("*/30 * * * * *");
                log.info("cron: {}", cronExpression);
                scheduleTask.schedulePushAlarm(cronExpression, pc);
            });
        return "다수 기기 알림 전송 성공 ! messages were sent successfully";
    }
}
