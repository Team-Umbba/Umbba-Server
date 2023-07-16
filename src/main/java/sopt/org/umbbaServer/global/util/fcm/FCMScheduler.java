package sopt.org.umbbaServer.global.util.fcm;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sopt.org.umbbaServer.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbbaServer.domain.parentchild.service.ParentchildService;
import sopt.org.umbbaServer.domain.qna.dao.QnADao;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.qna.model.Question;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMPushRequestDto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FCMScheduler {

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
                    String cronExpression = String.format("0 %s %s * * ?", pc.getPushTime().getMinute(), pc.getPushTime().getHour());
//                    String cronExpression = String.format("*/10 * * * * *");
                    log.info("cron: {}", cronExpression);
                    /*QnA todayQnA = qnADao.findQuestionByParentchildId(pc.getId()).orElseThrow(
                            () -> new CustomException(ErrorType.PARENTCHILD_HAVE_NO_QNALIST)
                    );*/
                    Optional<QnA> todayQnA = qnADao.findQuestionByParentchildId(pc.getId());
                    todayQnA.ifPresent(qna -> {
                        log.info("todayQnA: {}", qna.getQuestion().getTopic());

                        fcmService.schedulePushAlarm(cronExpression, qna.getQuestion(), pc.getId());  // cron 스케줄을 이용해 작업 예약
                    });

                    log.error("{}번째 Parentchild의 QnAList가 존재하지 않음!", pc.getId());
                });
        return "다수 기기 알림 전송 성공 ! messages were sent successfully";
    }
}
