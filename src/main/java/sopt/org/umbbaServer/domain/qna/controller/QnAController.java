package sopt.org.umbbaServer.domain.qna.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.GetInviteCodeResponseDto;
import sopt.org.umbbaServer.domain.parentchild.service.ParentchildService;
import sopt.org.umbbaServer.domain.qna.controller.dto.request.TodayAnswerRequestDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.QnAListResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.SingleQnAResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.GetMainViewResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.TodayQnAResponseDto;
import sopt.org.umbbaServer.domain.qna.service.QnAService;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.config.jwt.JwtProvider;
import sopt.org.umbbaServer.global.exception.SuccessType;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class QnAController {

    private final QnAService qnAService;

    @GetMapping("/qna/today")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TodayQnAResponseDto> getTodayQna(Principal principal) {

        return ApiResponse.success(SuccessType.GET_TODAY_QNA_SUCCESS, qnAService.getTodayQnA(JwtProvider.getUserFromPrincial(principal)));
    }

    @GetMapping("/qna/dummy")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse dummy() {
        qnAService.createQnA();

        return ApiResponse.success(SuccessType.GET_TODAY_QNA_SUCCESS);
    }

    @PostMapping("/qna/answer")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse answerTodayQuestion(
            Principal principal,
            @Valid @RequestBody TodayAnswerRequestDto request) {

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

}
