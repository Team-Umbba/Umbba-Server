package sopt.org.umbbaServer.domain.parentchild.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.request.OnboardingInviteRequestDto;
import sopt.org.umbbaServer.domain.parentchild.controller.dto.response.OnboardingInviteResponseDto;
import sopt.org.umbbaServer.domain.parentchild.service.ParentchildService;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.exception.SuccessType;

import javax.validation.Valid;

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
}