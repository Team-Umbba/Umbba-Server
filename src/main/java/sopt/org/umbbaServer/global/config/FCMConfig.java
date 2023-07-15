package sopt.org.umbbaServer.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMNotificationRequestDto;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Configuration
public class FCMConfig {

    @Value("${fcm.key.path}")
    private String SERVICE_ACCOUNT_JSON;

    // SDK 초기화: ADC를 사용하여 사용자 인증 정보 제공 -> 보내기 요청 승인 후에 사용 가능
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_JSON);
            InputStream serviceAccount = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.error("파이어베이스 서버와의 연결에 실패했습니다.");
            throw new CustomException(ErrorType.FIREBASE_CONNECTION_ERROR);
        }
    }

    // 여러 개의 파이어베이스 앱을 사용하는 경우
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {

        ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_JSON);
        InputStream refreshToken = resource.getInputStream();

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        if (!firebaseAppList.isEmpty() && firebaseAppList != null) {
            for (FirebaseApp app : firebaseAppList) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                }
            }
        } else {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options);
        }

        return FirebaseMessaging.getInstance(firebaseApp);


    }

    // TODO 플랫폼마다 별도의 설정이 필요한 경우 사용

    // Android
    public AndroidConfig TokenAndroidConfig(FCMNotificationRequestDto request) {
        return AndroidConfig.builder()
//                .setCollapseKey(request.getCollapseKey())
                .setNotification(AndroidNotification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody())
                        .build())
                .build();
    }

    // Apple
    public ApnsConfig TokenApnsConfig(FCMNotificationRequestDto request) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setAlert(
                                ApsAlert.builder()
                                        .setTitle(request.getTitle())
                                        .setBody(request.getBody())
//                                        .setLaunchImage(request.getImgUrl())
                                        .build()
                        )
//                        .setCategory(request.getCollapseKey())
                        .setSound("default")
                        .build())
                .build();
    }
}
