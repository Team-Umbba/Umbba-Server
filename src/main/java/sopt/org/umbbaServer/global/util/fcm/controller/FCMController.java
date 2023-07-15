package sopt.org.umbbaServer.global.util.fcm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.config.jwt.JwtProvider;
import sopt.org.umbbaServer.global.exception.SuccessType;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMPushRequestDto;
import sopt.org.umbbaServer.global.util.fcm.FCMService;

import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/alarm")
public class FCMController {

    private final FCMService fcmService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendNotificationByToken(@RequestBody FCMPushRequestDto request, Principal principal) throws IOException {

        return ApiResponse.success(SuccessType.PUSH_ALARM_SUCCESS, fcmService.pushAlarm(request, JwtProvider.getUserFromPrincial(principal)));
    }

    @PostMapping("/qna")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendScheduledTest() {
        fcmService.pushTodayQna();
        return ApiResponse.success(SuccessType.PUSH_ALARM_SUCCESS);
    }

}
