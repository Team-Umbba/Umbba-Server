package sopt.org.umbbaServer.global.util.fcm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.config.jwt.JwtProvider;
import sopt.org.umbbaServer.global.exception.SuccessType;
import sopt.org.umbbaServer.global.util.fcm.FCMScheduler;
import sopt.org.umbbaServer.global.util.fcm.FCMService;
import sopt.org.umbbaServer.global.util.fcm.controller.dto.FCMNotificationRequestDto;

import java.io.IOException;
import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/alarm")
public class FCMController {

    private final FCMService fcmService;
    private final FCMScheduler fcmScheduler;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendNotificationByToken(@RequestBody FCMNotificationRequestDto request, Principal principal) throws IOException {

        return ApiResponse.success(SuccessType.PUSH_ALARM_SUCCESS, fcmService.sendMessageTo(request, JwtProvider.getUserFromPrincial(principal)));
    }

    @PostMapping("/qna")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendScheduledTest() {
        fcmScheduler.pushTodayQna();
        return ApiResponse.success(SuccessType.PUSH_ALARM_SUCCESS);
    }

}