package sopt.org.umbbaServer.global.util.fcm.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * - Request
 * {
 *   "validate_only": boolean,
 *   "message": {
 *     object (Message)
 *   }
 * }
 *
 *
 * - Message
 * {
 *   "name": string,
 *   "data": {
 *     string: string,
 *     ...
 *   },
 *   "notification": {   ✅모든 플랫폼에서 사용할 기본 알림 템플릿
 *     object (Notification)
 *   },
 *   "android": {    FCM 연결 서버를 통해 전송된 메시지에 대한 Android 전용 옵션 TODO 이 부분을 서버 측에서 설정해줘야 하는지?
 *     object (AndroidConfig)
 *   },
 *   "webpush": {   Web 푸시 알림을 위한 webpush 프로토콘 옵션
 *     object (WebpushConfig)
 *   },
 *   "apns": {      Apple 푸시 알림 서비스 특정 옵션  TODO 이 부분을 서버 측에서 설정해줘야 하는지?
 *     object (ApnsConfig)
 *   },
 *   "fcm_options": {  모든 플랫폼에서 사용할 FCM SDK 기능 옵션용 템플릿
 *     object (FcmOptions)
 *   },
 *
 *   // Union field target can be only one of the following:
 *   "token": string,    메시지를 보낼 등록 토큰 (특정 클라이언트 대상)
 *   "topic": string,    Topic 발행의 경우, 사용
 *   "condition": string
 *   // End of list of possible types for union field target.
 * }
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FCMMessage {

    private boolean validateOnly;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification;   // 모든 모바일 OS에 통합으로 사용할 수 있는 Notification
        private String token;   // 특정 디바이스(클라이언트)에 알림을 보내기 위한 토큰
//        private Data data;
        private String topic;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private String name;
        private String description;
    }


}
