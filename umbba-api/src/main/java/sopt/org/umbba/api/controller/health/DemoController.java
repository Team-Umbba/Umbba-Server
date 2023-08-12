package sopt.org.umbba.api.controller.health;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbba.api.config.sqs.producer.SqsProducer;
import sopt.org.umbba.api.service.notification.NotificationService;
import sopt.org.umbba.api.service.qna.QnAService;
import sopt.org.umbba.common.exception.SuccessType;
import sopt.org.umbba.common.exception.dto.ApiResponse;
import sopt.org.umbba.common.sqs.dto.FCMPushRequestDto;

@RestController
@RequiredArgsConstructor
public class DemoController {

    private final QnAService qnAService;
    private final NotificationService notificationService;

    /**
     * 데모데이 테스트용 QnA리스트 세팅 API
     * - API 호출 시 4일차까지 일일문답을 완료했고, 5일차 답변을 할 차례로 만들기
     * - 5일차로 변경되는 시점에서 푸시메시지 전송
     * - 4일차까지의 is**Answer, **Answer 필드가 채워진 상태
     *
     * - 최대한 User 테이블만 보고 테스트할 수 있도록 설계
     */
    @PatchMapping("/demo/list/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse demoList(@PathVariable final Long userId) {

        qnAService.updateDemoList(userId);
        return ApiResponse.success(SuccessType.TEST_SUCCESS);
    }


    /**
     * 데모데이 테스트용 QnA 세팅 API
     * - API 호출할 때마다 일수가 증가하며 새로운 질문으로 업데이트
     * - 오늘의 질문 알림 푸시 함께 전송
     *
     */
    @PatchMapping("/demo/qna/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse demoQnA(@PathVariable final Long userId) {

        qnAService.todayUpdate(userId);
        return ApiResponse.success(SuccessType.TEST_SUCCESS);
    }

    @PatchMapping("/demo/qna/alarm/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse qnaAnswerAlarm(@PathVariable final Long userId, @RequestBody String question) {

        notificationService.pushOpponentReply(question, userId);
        return ApiResponse.success(SuccessType.TEST_SUCCESS);
    }

}
