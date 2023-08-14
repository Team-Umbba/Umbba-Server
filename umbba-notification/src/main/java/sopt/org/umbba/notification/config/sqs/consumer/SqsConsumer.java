package sopt.org.umbba.notification.config.sqs.consumer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sopt.org.umbba.common.sqs.MessageType;
import sopt.org.umbba.common.sqs.MessageUtils;
import sopt.org.umbba.common.sqs.dto.FCMPushRequestDto;
import sopt.org.umbba.common.sqs.dto.FirebaseDto;
import sopt.org.umbba.common.sqs.dto.SlackDto;
import sopt.org.umbba.notification.service.fcm.FCMService;
import sopt.org.umbba.notification.service.slack.SlackApi;

import javax.websocket.SendResult;
import java.util.Map;

/**
 * 큐 대기열에 있는 메시지 목록을 조회하여 받아오는(pull) 역할
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SqsConsumer {

    private final ObjectMapper objectMapper;
    private final FCMService fcmService;
    private final SlackApi slackApi;
    private static final String SQS_CONSUME_LOG_MESSAGE =
            "====> [SQS Queue Response]\n" + "info: %s\n" + "header: %s\n";


    // SQS로부터 메시지를 받는 Listener | 메시지를 받은 이후의 삭제 정책을 NEVER로 지정
    // -> 절대 삭제 요청을 보내지 않고, ack 메서드를 호출할 때 삭제 요청을 보냄
    @SqsListener(value = "${cloud.aws.sqs.notification.name}", deletionPolicy = SqsMessageDeletionPolicy.NEVER)
    public void consume(@Payload String payload, @Headers Map<String, String> headers, Acknowledgment ack) {
        try {
            log.info(MessageUtils.generate(SQS_CONSUME_LOG_MESSAGE, payload, headers));

            switch (headers.get(MessageType.MESSAGE_TYPE_HEADER)) {

                case MessageType.FCM_SINGLE:
                    FCMPushRequestDto singlePush = objectMapper.readValue(payload, FCMPushRequestDto.class);
                    fcmService.pushAlarm(singlePush);
                    break;

                case MessageType.FCM_MULTI:
                    FCMPushRequestDto multiPush = objectMapper.readValue(payload, FCMPushRequestDto.class);
                    fcmService.multipleSendByToken(multiPush);
                    break;

                case MessageType.SLACK:
                    SlackDto slackDto = objectMapper.readValue(payload, SlackDto.class);
                    slackApi.sendAlert(slackDto.getError(), slackDto.getRequestMethod(), slackDto.getRequestURI());
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        ack.acknowledge();
    }

}