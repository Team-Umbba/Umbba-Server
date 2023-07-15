package sopt.org.umbbaServer.global.util.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMMessage;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMPushRequestDto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    @Value("${fcm.key.path}")
    private String SERVICE_ACCOUNT_JSON;
    @Value("${fcm.api.url}")
    private String FCM_API_URL;
    @Value("${fcm.topic}")
    private String topic;

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;


    // Firebase에서 Access Token 가져오기
    private String getAccessToken() throws IOException {

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(SERVICE_ACCOUNT_JSON).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        log.info("getAccessToken() - googleCredentials: {} ", googleCredentials.getAccessToken().getTokenValue());

        return googleCredentials.getAccessToken().getTokenValue();
    }

    // FCM Service에 메시지를 수신하는 함수 (헤더와 바디 직접 만들기)
    @Transactional
    public String pushAlarm(FCMPushRequestDto request, Long userId) throws IOException {

        // TODO 같은 Parentchild ID를 가진 User를 찾은 후, 이들에 대한 토큰 리스트로 동일한 알림 메시지 전송하도록
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );
        user.updateFcmToken(request.getTargetToken());

        String message = makeMessage(request);
        sendPushMessage(message);
        return "알림을 성공적으로 전송했습니다. targetUserId = " + request.getTargetToken();
    }

    private void sendPushMessage(String message) throws IOException {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request httpRequest = new Request.Builder()
                .url(FCM_API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(httpRequest).execute();

        log.info("알림 전송: {}", response.body().string());
    }

    // 요청 파라미터를 FCM의 body 형태로 만들어주는 메서드
    private String makeMessage(FCMPushRequestDto request) throws JsonProcessingException {

        FCMMessage fcmMessage = FCMMessage.builder()
                .message(FCMMessage.Message.builder()
                        .token(request.getTargetToken())
                        .notification(FCMMessage.Notification.builder()
                                .title(request.getTitle())
                                .body(request.getBody())
                                .image(null)
                                .build())
                        .build()
                ).validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    // 따로 만들어둔 메세지 템플릿 이용해서 전송할 때 사용하는 알람
    private String makeMessage(FCMPushRequestDto request, Long userId) throws FirebaseMessagingException, JsonProcessingException {

        Optional<User> user = userRepository.findByFcmToken(request.getTargetToken());

        if (user.isEmpty()) {
            user = userRepository.findById(userId);
            user.orElseThrow(
                    () -> new CustomException(ErrorType.INVALID_USER)
            );
            user.get().updateFcmToken(request.getTargetToken());
        }

        FCMMessage fcmMessage = FCMMessage.builder()
                .message(FCMMessage.Message.builder()
                        .token(user.get().getFcmToken())
                        .topic(topic)
                        .notification(FCMMessage.Notification.builder()
                                .title(request.getTitle())
                                .body(request.getBody())
                                .image(null)
                                .build())
                        .build()
                ).validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }


    @Scheduled(cron = "0 0 23 * * ?")
    public void pushTodayQna() {

        try {
            log.info("오늘의 질문 알람 - 유저마다 보내는 시간 다름");
        /*List<QnA> qnAList = qnADao.findQnASByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorType.USER_HAVE_NO_QNALIST)
        );
        QnA lastQna = qnAList.get(qnAList.size()-1);*/
            String message = makeMessage(FCMPushRequestDto.sendTodayQna("targetToken","section", "question"), 1L);
            sendPushMessage(message);
        } catch (IOException e) {
            log.error("푸시메시지 전송 실패 - IOException: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        } catch (FirebaseMessagingException e) {
            log.error("푸시메시지 전송 실패 - FirebaseMessagingException: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        }
    }

    public void pushOpponentReply(String question, Long userId) {

        // 상대 측 유저의 FCM 토큰 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );

        try {
            log.info("상대방 답변 완료!");
            String message = makeMessage(FCMPushRequestDto.sendOpponentReply(user.getFcmToken(), question), userId);
            sendPushMessage(message);
        } catch (IOException e) {
            log.error("푸시메시지 전송 실패 - IOException: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        } catch (FirebaseMessagingException e) {
            log.error("푸시메시지 전송 실패 - FirebaseMessagingException: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        }
    }


    // 단일 기기에 알림 메시지 전송
    public String sendNotificationByToken(FCMPushRequestDto request) {

        // TODO 같은 Parentchild ID를 가진 User를 찾은 후, 이들에 대한 토큰 리스트로 동일한 알림 메시지 전송하도록
        User user = userRepository.findByFcmToken(request.getTargetToken()).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );

        if (user.getFcmToken() != null) {

            Notification notification = Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getBody())
                    .build();

            // 메시지 만들기 TODO List<Message> 로도 구현 가능
            Message message = Message.builder()
                    .setToken(user.getFcmToken())
                    .setNotification(notification)
//                    .putAllData(request.getData())
                    .build();

            try {
                FirebaseMessaging.getInstance().send(message);
                return "알림을 성공적으로 전송했습니다. targetUserId = " + request.getTargetToken();
            } catch (FirebaseMessagingException e) {
                log.error("알림 전송 실패 - {}", e);
                return "알림 전송에 실패했습니다. targetUserId = " + request.getTargetToken();
            }
        }

        throw new CustomException(ErrorType.INVALID_FIREBASE_TOKEN);
    }

    // 다수의 기기에 알림 메시지 전송
    /*public void multipleSendByToken(FCMNotificationRequestDto request) throws FirebaseMessagingException {

        List<String> tokenList = IntStream.rangeClosed(1, 30).mapToObj(
                index -> request.getFire
        )
    }*/


}
