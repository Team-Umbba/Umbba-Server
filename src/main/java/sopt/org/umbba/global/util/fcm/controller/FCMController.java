package sopt.org.umbba.global.util.fcm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbba.global.common.dto.ApiResponse;
import sopt.org.umbba.global.config.jwt.JwtProvider;
import sopt.org.umbba.global.exception.SuccessType;
import sopt.org.umbba.global.util.fcm.FCMScheduler;
import sopt.org.umbba.global.util.fcm.FCMService;
import sopt.org.umbba.global.util.fcm.controller.dto.FCMPushRequestDto;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/alarm")
@RequiredArgsConstructor
public class FCMController {

    private final FCMService fcmService;
    private final FCMScheduler fcmScheduler;


    /**
     * 새로운 질문이 도착했음을 알리는 푸시 알림 활성화 API
     * 실제로는 초대 받는측의 온보딩이 완료되었을 때 호출됨
     */
    @PostMapping("/qna")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendTopicScheduledTest() {
        return ApiResponse.success(SuccessType.PUSH_ALARM_PERIODIC_SUCCESS, fcmScheduler.pushTodayQna());
    }

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

        return ApiResponse.success(SuccessType.PUSH_ALARM_SUCCESS, fcmService.pushAlarm(request, JwtProvider.getUserFromPrincial(principal)));
    }

    /**
     * 동시에 여러 사람에게 푸시 알림을 보내보는 테스트용 API (주기적 알람 전송에 사용)
     */
    @PostMapping("/parentchild")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendMultiScheduledTest() {
        return ApiResponse.success(SuccessType.PUSH_ALARM_SUCCESS, fcmService.multipleSendByToken(FCMPushRequestDto.sendTodayQna("section", "question") ,93L));
    }


}
