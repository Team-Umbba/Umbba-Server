package sopt.org.umbba.api.controller.closer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbba.api.config.jwt.JwtProvider;
import sopt.org.umbba.api.controller.closer.dto.request.TodayCloserAnswerRequestDto;
import sopt.org.umbba.api.controller.closer.dto.response.TodayCloserQnAResponseDto;
import sopt.org.umbba.api.service.closer.CloserService;
import sopt.org.umbba.common.exception.SuccessType;
import sopt.org.umbba.common.exception.dto.ApiResponse;

import javax.validation.Valid;
import java.security.Principal;

import static sopt.org.umbba.common.exception.SuccessType.ANSWER_TODAY_CLOSER_QUESTION_SUCCESS;
import static sopt.org.umbba.common.exception.SuccessType.PASS_TO_NEXT_CLOSER_QUESTION_SUCCESS;

@Slf4j
@RestController
@RequestMapping("/closer")
@RequiredArgsConstructor
public class CloserController {

    private final CloserService closerService;

    @GetMapping("/today")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TodayCloserQnAResponseDto> getTodayCloserQnA(Principal principal) {
        return ApiResponse.success(SuccessType.GET_TODAY_CLOSER_QNA_SUCCESS, closerService.getTodayCloserQnA(JwtProvider.getUserFromPrincial(principal)));
    }

    @PatchMapping("/answer")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<?> answerTodayCloserQnA(Principal principal, @Valid @RequestBody final TodayCloserAnswerRequestDto request) {
        closerService.answerTodayCloserQnA(JwtProvider.getUserFromPrincial(principal), request);
        return ApiResponse.success(ANSWER_TODAY_CLOSER_QUESTION_SUCCESS);
    }

    @PatchMapping("/next")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<?> passToNextCloserQnA(Principal principal) {
        closerService.passToNextCloserQnA(JwtProvider.getUserFromPrincial(principal));
        return ApiResponse.success(PASS_TO_NEXT_CLOSER_QUESTION_SUCCESS);
    }
}
