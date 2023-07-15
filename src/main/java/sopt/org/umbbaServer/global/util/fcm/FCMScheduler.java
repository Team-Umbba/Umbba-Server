package sopt.org.umbbaServer.global.util.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMPushRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMScheduler {

//    @Value("${fcm.key.firebase-create-scoped}")
    String firebaseCreateScoped;  // TODO 미사용 -> 삭제할 것

    @Value("${fcm.topic}")
    String topic;


    @Scheduled(cron = "0 0 23 * * ?")
    public void pushTodayQna() {
        try {
            log.info("오늘의 질문 알람 - 유저마다 보내는 시간 다름");
        /*List<QnA> qnAList = qnADao.findQnASByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorType.USER_HAVE_NO_QNALIST)
        );
        QnA lastQna = qnAList.get(qnAList.size()-1);*/
            pushAlarm(FCMPushRequestDto.sendTodayQna("section", "question"));
        } catch (FirebaseMessagingException e) {
            log.error("푸시메시지 전송 실패!: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        }
    }

    public void pushOpponentReply(String question) {
        try {
            log.info("상대방 답변 완료!");
            pushAlarm(FCMPushRequestDto.sendOpponentReply(question));
        } catch (FirebaseMessagingException e) {
            log.error("푸시메시지 전송 실패!: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        }
    }

    private void pushAlarm(FCMPushRequestDto.PushMessage data) throws FirebaseMessagingException {

        Notification notification = Notification.builder()
                .setTitle(data.getTitle())
                .setBody(data.getBody())
                .build();

        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(notification)
                .build();

        FirebaseMessaging.getInstance().send(message);
        log.info("firebase messaging send() 성공 : {}", message);
    }

}
