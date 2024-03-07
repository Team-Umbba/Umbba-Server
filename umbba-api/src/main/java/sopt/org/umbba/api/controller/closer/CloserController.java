package sopt.org.umbba.api.controller.closer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.umbba.api.config.jwt.JwtProvider;
import sopt.org.umbba.api.controller.closer.dto.response.TodayCloserQnAResponseDto;
import sopt.org.umbba.api.service.closer.CloserService;
import sopt.org.umbba.common.exception.SuccessType;
import sopt.org.umbba.common.exception.dto.ApiResponse;

import java.security.Principal;

@Slf4j
@RestController("/closer")
@RequiredArgsConstructor
public class CloserController {

    private final CloserService closerService;

    @PatchMapping("/today")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TodayCloserQnAResponseDto> inviteRelation(Principal principal) {
        return ApiResponse.success(SuccessType.GET_TODAY_CLOSER_QNA_SUCCESS, closerService.getTodayCloserQnA(JwtProvider.getUserFromPrincial(principal)));
    }
}
