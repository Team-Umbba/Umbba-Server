package sopt.org.umbbaServer.domain.parentchild.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.InviteCodeRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingInviteRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.InviteResultResponeDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingInviteResponseDto;
import sopt.org.umbbaServer.domain.parentchild.service.ParentchildService;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.config.jwt.JwtProvider;
import sopt.org.umbbaServer.global.exception.SuccessType;

import javax.validation.Valid;
import java.security.Principal;

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

    @PostMapping("/match")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<InviteResultResponeDto> inviteRelation(@Valid @RequestBody InviteCodeRequestDto request, Principal principal) {
        Long userId = JwtProvider.getUserFromPrincial(principal);
        return ApiResponse.success(SuccessType.MATCH_PARENT_CHILD_SUCCESS, parentchildService.matchRelation(userId, request));
    }
}
