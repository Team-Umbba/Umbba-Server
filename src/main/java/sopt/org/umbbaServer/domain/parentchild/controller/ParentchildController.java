package sopt.org.umbbaServer.domain.parentchild.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.InviteCodeRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingInviteRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingReceiveRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.InviteResultResponseDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingReceiveResponseDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingInviteResponseDto;
import sopt.org.umbbaServer.domain.parentchild.service.ParentchildService;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.config.jwt.JwtProvider;
import sopt.org.umbbaServer.global.exception.SuccessType;

import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ParentchildController {

    private final ParentchildService parentchildService;

    @PostMapping("/onboard/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OnboardingInviteResponseDto> onboardInvite(@RequestBody @Valid final OnboardingInviteRequestDto request, Principal principal) {
        return ApiResponse.success(SuccessType.CREATE_PARENT_CHILD_SUCCESS, parentchildService.onboardInvite(JwtProvider.getUserFromPrincial(principal), request));
    }

    @PatchMapping("/onboard/match")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<InviteResultResponseDto> inviteRelation(@RequestBody @Valid final InviteCodeRequestDto request, Principal principal) {
        log.info("getUserFromPrincipal에는 문제가 없어요 - 요청 초대코드: {}", request.getInviteCode());

//        Long userId = JwtProvider.getUserFromPrincial(principal);
        log.info("ParentchlidController 실행 - 요청 초대코드: {}", request.getInviteCode());
        return ApiResponse.success(SuccessType.MATCH_PARENT_CHILD_SUCCESS, parentchildService.matchRelation(JwtProvider.getUserFromPrincial(principal), request));

    }

    @PatchMapping("/onboard/receive")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<OnboardingReceiveResponseDto> onboardReceive(@RequestBody @Valid final OnboardingReceiveRequestDto request, Principal principal) {
        return ApiResponse.success(SuccessType.CREATE_PARENT_CHILD_SUCCESS, parentchildService.onboardReceive(JwtProvider.getUserFromPrincial(principal), request));
    }



}
