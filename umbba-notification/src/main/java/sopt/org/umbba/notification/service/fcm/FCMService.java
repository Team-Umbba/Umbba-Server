package sopt.org.umbba.notification.service.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.parentchild.dao.ParentchildDao;
import sopt.org.umbba.domain.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbba.domain.domain.qna.QnA;
import sopt.org.umbba.domain.domain.user.SocialPlatform;
import sopt.org.umbba.domain.domain.user.User;
import sopt.org.umbba.domain.domain.user.repository.UserRepository;
import sopt.org.umbba.notification.service.fcm.dto.FCMMessage;
import sopt.org.umbba.common.sqs.dto.FCMPushRequestDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PessimisticLockException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * 서버에서 파이어베이스로 전송이 잘 이루어지는지 테스트하기 위한 컨트롤러
 */
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
    private final ParentchildDao parentchildDao;
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

    // 요청 파라미터를 FCM의 body 형태로 만들어주는 메서드 [단일 기기]
    public String makeMessage(FCMPushRequestDto request) throws JsonProcessingException {

        FCMMessage fcmMessage = FCMMessage.builder()
                .message(FCMMessage.Message.builder()
                        .token(request.getTargetToken())   // 1:1 전송 시 반드시 필요한 대상 토큰 설정
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

    // 따로 만들어둔 메세지 템플릿 이용해서 전송할 때 사용하는 알람 [Topic 구독]
    String makeMessage(FCMPushRequestDto request, Long userId) throws FirebaseMessagingException, JsonProcessingException {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorType.INVALID_USER)
        );


        FCMMessage fcmMessage = FCMMessage.builder()
                .message(FCMMessage.Message.builder()
                                .token(user.getFcmToken())
//                        .topic(topic)   // 토픽 구동에서 반드시 필요한 설정 (token 지정 x)
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

    // FCM Service에 메시지를 수신하는 함수 (헤더와 바디 직접 만들기) -> 상대 답변 알람 전송에 사용
    @Transactional
    public String pushAlarm(FCMPushRequestDto request) throws IOException {

        String message = makeMessage(request);
        sendPushMessage(message);
        return "알림을 성공적으로 전송했습니다. targetUserId = " + request.getTargetToken();
    }

    public void sendPushMessage(String message) throws IOException {

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request httpRequest = new Request.Builder()
                .url(FCM_API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(httpRequest).execute();

        log.info("단일 기기 알림 전송 성공 ! successCount: 1 messages were sent successfully");
        log.info("알림 전송: {}", response.body().string());
    }

    // 다수의 기기(부모자식 ID에 포함된 유저 2명)에 알림 메시지 전송 -> 주기적 알림 전송에서 사용
    public String multipleSendByToken(FCMPushRequestDto request) {

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .setImage(null)
                        .build())
                .addAllTokens(request.getTargetTokenList())
                .build();

        log.info("message: {}", request.getTitle() +" "+ request.getBody());

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            log.info("다수 기기 알림 전송 성공 ! successCount: " + response.getSuccessCount() + " messages were sent successfully");
            log.info("알림 전송: {}", response.getResponses().toString());

            return "알림을 성공적으로 전송했습니다. \ntargetUserId = 1." + request.getTargetTokenList().get(0) + ", \n\n2." + request.getTargetTokenList().get(1);
        } catch (FirebaseMessagingException e) {
            log.error("다수기기 푸시메시지 전송 실패 - FirebaseMessagingException: {}", e.getMessage());
            throw new CustomException(ErrorType.FAIL_TO_SEND_PUSH_ALARM);
        }
    }




    /**
     * 사용 안하는 함수들
     */

    // Topic 구독 설정 - application.yml에서 topic명 관리
    // 단일 요청으로 최대 1000개의 기기를 Topic에 구독 등록 및 취소할 수 있다.
    public void subscribe() throws FirebaseMessagingException {
        // These registration tokens come from the client FCM SDKs.
        // TODO Parentchild 테이블 탐색 후 주기적으로 알림 쏴주기
        List<String> registrationTokens = Arrays.asList(
                "YOUR_REGISTRATION_TOKEN_1",
                // ...
                "YOUR_REGISTRATION_TOKEN_n"
        );

        // Subscribe the devices corresponding to the registration tokens to the topic.
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
                registrationTokens, topic);

        System.out.println(response.getSuccessCount() + " tokens were subscribed successfully");
    }

    // Topic 구독 취소
    public void unsubscribe() throws FirebaseMessagingException {
        // These registration tokens come from the client FCM SDKs.
        List<String> registrationTokens = Arrays.asList(
                "YOUR_REGISTRATION_TOKEN_1",
                // ...
                "YOUR_REGISTRATION_TOKEN_n"
        );

        // Unsubscribe the devices corresponding to the registration tokens from the topic.
        TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
                registrationTokens, topic);

        System.out.println(response.getSuccessCount() + " tokens were unsubscribed successfully");
    }
}
