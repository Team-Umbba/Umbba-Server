package sopt.org.umbba.notification.service.fcm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbba.common.exception.SuccessType;
import sopt.org.umbba.common.exception.dto.ApiResponse;
import sopt.org.umbba.common.sqs.dto.FCMPushRequestDto;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class FCMController {

    private final FCMService fcmService;

    /**
     * 장난용 푸시 알림 활성화 API
     */
//    @PostMapping("/drink")
//    @ResponseStatus(HttpStatus.OK)
//    public ApiResponse drinkAlarm() {
//        return ApiResponse.success(SuccessType.PUSH_ALARM_PERIODIC_SUCCESS, fcmScheduler.drink());
//    }


    /**
     * 헤더와 바디를 직접 만들어 알림을 전송하는 테스트용 API (상대 답변 알람 전송에 사용)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendNotificationByToken(@RequestBody FCMPushRequestDto request, Principal principal) throws IOException {

        return ApiResponse.success(SuccessType.PUSH_ALARM_SUCCESS, fcmService.pushAlarm(request));
    }

    /**
     * 동시에 여러 사람에게 푸시 알림을 보내보는 테스트용 API (주기적 알람 전송에 사용)
     */
    @PostMapping("/parentchild")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendMultiScheduledTest() {
        return ApiResponse.success(SuccessType.PUSH_ALARM_SUCCESS, fcmService.multipleSendByToken(FCMPushRequestDto.sendTodayQna(List.of("token1", "token2"),"section", "question")));
    }


}
