package sopt.org.umbba.api.controller.qna;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbba.api.config.jwt.JwtProvider;
import sopt.org.umbba.api.controller.qna.dto.request.TodayAnswerRequestDto;
import sopt.org.umbba.api.controller.qna.dto.response.*;
import sopt.org.umbba.api.service.qna.QnAService;
import sopt.org.umbba.api.service.scheduler.FCMScheduler;
import sopt.org.umbba.common.exception.SuccessType;
import sopt.org.umbba.common.exception.dto.ApiResponse;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class QnAController {

    private final QnAService qnAService;
    private final FCMScheduler fcmScheduler;


    @GetMapping("/qna/today")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TodayQnAResponseDto> getTodayQna(Principal principal) {

        return ApiResponse.success(SuccessType.GET_TODAY_QNA_SUCCESS, qnAService.getTodayQnA(JwtProvider.getUserFromPrincial(principal)));
    }


    @PostMapping("/qna/answer")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse answerTodayQuestion(
            Principal principal,
            @Valid @RequestBody final TodayAnswerRequestDto request) {

        qnAService.answerTodayQuestion(JwtProvider.getUserFromPrincial(principal), request);

        return ApiResponse.success(SuccessType.ANSWER_TODAY_QUESTION_SUCCESS);
    }

    @GetMapping("/qna/list/{sectionId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<QnAListResponseDto>> getQnaList(
            Principal principal,
            @PathVariable(name = "sectionId") Long sectionId) {

        return ApiResponse.success(SuccessType.GET_QNA_LIST_SUCCESS,
                qnAService.getQnaList(JwtProvider.getUserFromPrincial(principal), sectionId));
    }

    @GetMapping("/qna/{qnaId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SingleQnAResponseDto> getSingleQna(
            Principal principal,
            @PathVariable(name = "qnaId") Long qnaId) {

        return ApiResponse.success(SuccessType.GET_SINGLE_QNA_SUCCESS,
                qnAService.getSingleQna(JwtProvider.getUserFromPrincial(principal), qnaId));
    }

    @GetMapping("/home")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetMainViewResponseDto> home(Principal principal) {

        return ApiResponse.success(SuccessType.GET_MAIN_HOME_SUCCESS, qnAService.getMainInfo(JwtProvider.getUserFromPrincial(principal)));
    }

    @GetMapping("/home/case")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetInvitationResponseDto> invitation(Principal principal) {

        return ApiResponse.success(SuccessType.GET_INVITE_CODE_SUCCESS, qnAService.getInvitation(JwtProvider.getUserFromPrincial(principal)));
    }


    /**
     * 새로운 질문이 도착했음을 알리는 푸시 알림 활성화 API
     * 실제로는 초대 받는측의 온보딩이 완료되었을 때 호출됨
     */
    @PostMapping("/qna")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse sendTopicScheduledTest() {
        return ApiResponse.success(SuccessType.PUSH_ALARM_PERIODIC_SUCCESS, fcmScheduler.pushTodayQna());
    }

}
