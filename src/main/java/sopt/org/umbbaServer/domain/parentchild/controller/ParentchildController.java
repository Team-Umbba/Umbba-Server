package sopt.org.umbbaServer.domain.parentchild.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.InviteCodeRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingInviteRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingReceiveRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.InviteResultResponeDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingReceiveResponseDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingInviteResponseDto;
import sopt.org.umbbaServer.domain.parentchild.service.ParentchildService;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.exception.SuccessType;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/onboard")
@RequiredArgsConstructor
public class ParentchildController {

    private final ParentchildService parentchildService;

    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OnboardingInviteResponseDto> onboardInvite(@Valid @RequestBody OnboardingInviteRequestDto request) {
        return ApiResponse.success(SuccessType.CREATE_PARENT_CHILD_SUCCESS, parentchildService.onboardInvite(request));
    }

    @PatchMapping("/match")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<InviteResultResponeDto> inviteRelation(@Valid @RequestBody InviteCodeRequestDto request) {
        log.info("getUserFromPrincipal에는 문제가 없어요 - 요청 초대코드: {}", request.getInviteCode());

//        Long userId = JwtProvider.getUserFromPrincial(principal);
        log.info("ParentchlidController 실행 - 요청 초대코드: {}", request.getInviteCode());
        return ApiResponse.success(SuccessType.MATCH_PARENT_CHILD_SUCCESS, parentchildService.matchRelation(request.getUserId(), request));

    }

    @PatchMapping("/receive")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<OnboardingReceiveResponseDto> onboardReceive(@Valid @RequestBody OnboardingReceiveRequestDto request) {
        return ApiResponse.success(SuccessType.CREATE_PARENT_CHILD_SUCCESS, parentchildService.onboardReceive(request));
    }


}
