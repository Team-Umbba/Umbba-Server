package sopt.org.umbba.api.controller.parentchild;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbba.api.config.jwt.JwtProvider;
import sopt.org.umbba.api.controller.parentchild.dto.request.InviteCodeRequestDto;
import sopt.org.umbba.api.controller.parentchild.dto.request.OnboardingInviteRequestDto;
import sopt.org.umbba.api.controller.parentchild.dto.request.OnboardingReceiveRequestDto;
import sopt.org.umbba.api.controller.parentchild.dto.response.InviteResultResponseDto;
import sopt.org.umbba.api.controller.parentchild.dto.response.OnboardingInviteResponseDto;
import sopt.org.umbba.api.controller.parentchild.dto.response.OnboardingReceiveResponseDto;
import sopt.org.umbba.api.service.parentchild.ParentchildService;
import sopt.org.umbba.api.service.qna.QnAService;
import sopt.org.umbba.common.exception.SuccessType;
import sopt.org.umbba.common.exception.dto.ApiResponse;


import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParentchildController {

    private final ParentchildService parentchildService;
    private final QnAService qnAService;

    @PostMapping("/onboard/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OnboardingInviteResponseDto> onboardInvite(@RequestBody @Valid final OnboardingInviteRequestDto request, Principal principal) {

        Long userId = JwtProvider.getUserFromPrincial(principal);
        OnboardingInviteResponseDto response = parentchildService.onboardInvite(userId, request);
        qnAService.filterFirstQuestion(userId);

        return ApiResponse.success(SuccessType.CREATE_PARENT_CHILD_SUCCESS, response);
    }

    @PatchMapping("/onboard/match")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InviteResultResponseDto> inviteRelation(@RequestBody @Valid final InviteCodeRequestDto request, Principal principal) {
        return ApiResponse.success(SuccessType.MATCH_PARENT_CHILD_SUCCESS, parentchildService.matchRelation(JwtProvider.getUserFromPrincial(principal), request));
    }

    @PatchMapping("/onboard/receive")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OnboardingReceiveResponseDto> onboardReceive(@RequestBody @Valid final OnboardingReceiveRequestDto request, Principal principal) throws InterruptedException {

        Long userId = JwtProvider.getUserFromPrincial(principal);
        OnboardingReceiveResponseDto response = parentchildService.onboardReceive(userId, request);
        qnAService.filterAllQuestion(userId);

        return ApiResponse.success(SuccessType.CREATE_PARENT_CHILD_SUCCESS, response);
    }



}
