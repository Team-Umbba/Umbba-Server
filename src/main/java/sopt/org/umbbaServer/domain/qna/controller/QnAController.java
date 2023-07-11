package sopt.org.umbbaServer.domain.qna.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.GetInviteCodeResponseDto;
import sopt.org.umbbaServer.domain.parentchild.service.ParentchildService;
import sopt.org.umbbaServer.domain.qna.controller.dto.request.TodayAnswerRequestDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.GetMainViewResponseDto;
import sopt.org.umbbaServer.domain.qna.controller.dto.response.TodayQnAResponseDto;
import sopt.org.umbbaServer.domain.qna.service.QnAService;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.config.jwt.JwtProvider;
import sopt.org.umbbaServer.global.exception.SuccessType;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class QnAController {

    private final QnAService qnAService;
    private final ParentchildService parentchildService;   // TODO QnAController에서는 QnAService만 주입받도록 수정

    @GetMapping("/qna")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TodayQnAResponseDto> getTodayQna(Principal principal) {

        return ApiResponse.success(SuccessType.GET_TODAY_QNA_SUCCESS, qnAService.getTodayQnA(JwtProvider.getUserFromPrincial(principal)));
    }

    @GetMapping("/dummy")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse dummy() {
        qnAService.createQnA();

        return ApiResponse.success(SuccessType.GET_TODAY_QNA_SUCCESS);
    }

    @PostMapping("/qna")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse answerTodayQuestion(
            Principal principal,
            @Valid @RequestBody TodayAnswerRequestDto request) {

        qnAService.answerTodayQuestion(request, JwtProvider.getUserFromPrincial(principal));

        return ApiResponse.success(SuccessType.ANSWER_TODAY_QUESTION_SUCCESS);
    }

    // TODO HomeController로 따로 뺄지?
    @GetMapping("/home")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetMainViewResponseDto> home(Principal principal) {

        return ApiResponse.success(SuccessType.GET_MAIN_HOME_SUCCESS, qnAService.getMainInfo(JwtProvider.getUserFromPrincial(principal)));
    }

    @GetMapping("/home/invite")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<GetInviteCodeResponseDto> invitation(Principal principal) {

        return ApiResponse.success(SuccessType.GET_INVITE_CODE_SUCCESS, parentchildService.getInvitation(JwtProvider.getUserFromPrincial(principal)));
    }
}
