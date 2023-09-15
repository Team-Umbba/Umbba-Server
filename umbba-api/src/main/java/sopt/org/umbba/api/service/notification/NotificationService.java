package sopt.org.umbba.api.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbba.api.config.sqs.producer.SqsProducer;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;
import sopt.org.umbba.common.sqs.dto.FCMPushRequestDto;
import sopt.org.umbba.common.sqs.dto.ScheduleDto;
import sopt.org.umbba.common.sqs.dto.SlackDto;
import sopt.org.umbba.domain.domain.user.User;
import sopt.org.umbba.domain.domain.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * SQS 대기열로 알림 메시지를 추가
 */
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class NotificationService {

    private final SqsProducer sqsProducer;
    private final UserRepository userRepository;

    public void pushOpponentReply(String question, Long userId) {

        // 상대 측 유저의 FCM 토큰 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );

        log.info("상대방 답변 완료!");
        sqsProducer.produce(FCMPushRequestDto.sendOpponentReply(user.getFcmToken(), question));

        /* try {
            log.info("상대방 답변 완료!");
            sqsProducer.produce(FCMPushRequestDto.sendOpponentReply(user.getFcmToken(), question));
        } catch (IOException e) {
            log.error("푸시메시지 전송 실패 - IOException: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        } catch (FirebaseMessagingException e) {
            log.error("푸시메시지 전송 실패 - FirebaseMessagingException: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        }*/
    }

    public void activateTodayQna() {
        sqsProducer.produce(ScheduleDto.of());
    }

    public void pushOpponentRemind(Long userId) {

        // 상대 측 유저의 FCM 토큰 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );

        log.info("리마인드할 상대방 조회 완료!");
        sqsProducer.produce(FCMPushRequestDto.sendOpponentRemind(user.getFcmToken(), "REMIND"));
    }

    public void sendExceptionToSlack(Exception e, HttpServletRequest request) {
        sqsProducer.produce(SlackDto.of(e, request.getMethod(), request.getRequestURI()));
    }


}
