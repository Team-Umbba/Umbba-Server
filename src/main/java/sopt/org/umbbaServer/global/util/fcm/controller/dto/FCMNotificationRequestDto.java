package sopt.org.umbbaServer.global.util.fcm.controller.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FCMNotificationRequestDto {

    private String targetToken;
    private String title;
    private String body;
}
