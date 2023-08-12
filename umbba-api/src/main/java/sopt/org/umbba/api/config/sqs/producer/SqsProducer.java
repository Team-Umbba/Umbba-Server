package sopt.org.umbba.api.config.sqs.producer;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sopt.org.umbba.common.sqs.MessageType;
import sopt.org.umbba.common.sqs.MessageUtils;
import sopt.org.umbba.common.sqs.dto.MessageDto;

import java.util.Map;
import java.util.UUID;

/**
 * 큐에 메시지를 보내는 역할:  API 서버에서 이벤트가 발생할 떄 푸시알림 전송
 * -> 처음 SQS 대기열 생성에서 설정해둔 사항이 여기서 적용 (지연시간, 메시지 수신 대기 등)
 *
 * 1. 처리할 작업 메시지를 SQS에 등록
 * 2. 큐에서 메시지를 소비(consume)하는 것을 실패한 경우, DLQ로 전송
 *
 * TODO 기존에 푸시알림을 파이어베이스로 보내기 위해 호출했던 함수를 SQS Producer로 대체
 */
@Slf4j
@Component
public class SqsProducer {

    @Value("${cloud.aws.sqs.notification.url}")
    private String NOTIFICATION_URL;

    private static final String GROUP_ID = "sqs";
    private final ObjectMapper objectMapper;
    private final AmazonSQS amazonSqs;
    private static final String SQS_QUEUE_REQUEST_LOG_MESSAGE = "====> [SQS Queue Request] : %s ";

    public SqsProducer(ObjectMapper objectMapper, AmazonSQS amazonSqs) {
        this.objectMapper = objectMapper;
        this.amazonSqs = amazonSqs;
    }


    public void produce(MessageDto message) {
        try {
            SendMessageRequest request = new SendMessageRequest(NOTIFICATION_URL,
                    objectMapper.writeValueAsString(message))
//                    .withMessageGroupId(GROUP_ID)
//                    .withMessageDeduplicationId(UUID.randomUUID().toString())  // TODO UUID Random String으로 변경
                    .withMessageAttributes(createMessageAttributes(message.getType()));

            amazonSqs.sendMessage(request);
            log.info(MessageUtils.generate(SQS_QUEUE_REQUEST_LOG_MESSAGE, request));

        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, MessageAttributeValue> createMessageAttributes(String type) {

        return Map.of(MessageType.MESSAGE_TYPE_HEADER, new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(type));
    }


    /*  Queue에 단일 메시지를 보내는 함수 -> SQS 실습에서 사용한 함수
    public SendResult<String> sendMessage(String groupId, String message) {
//        Message<String> newMessage = MessageBuilder.withPayload(message).build();
        System.out.println("Sender: " + message);
        return queueMessagingTemplate.send(to -> to
                .queue(QUEUE_NAME)
                .messageGroupId(groupId)
                .messageDeduplicationId(groupId)
                .payload(message));
    }
     */
}