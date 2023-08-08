package sopt.org.umbba.api.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sopt.org.umbba.api.config.jwt.JwtProvider;
import sopt.org.umbba.api.config.jwt.TokenDto;
import sopt.org.umbba.api.controller.user.dto.request.RefreshRequestDto;
import sopt.org.umbba.api.controller.user.dto.request.SocialLoginRequestDto;
import sopt.org.umbba.api.controller.user.dto.response.UserLoginResponseDto;
import sopt.org.umbba.api.service.user.AuthService;
import sopt.org.umbba.api.service.user.social.kakao.KakaoLoginService;
import sopt.org.umbba.common.exception.SuccessType;
import sopt.org.umbba.common.exception.dto.ApiResponse;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserLoginResponseDto> login(
            @RequestHeader("Authorization") String socialAccessToken,
            @RequestBody final SocialLoginRequestDto request) throws NoSuchAlgorithmException, InvalidKeySpecException {

        return ApiResponse.success(SuccessType.LOGIN_SUCCESS, authService.login(socialAccessToken, request));
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<TokenDto> reissue(
            @RequestHeader("Authorization") String refreshToken,
            @RequestBody final RefreshRequestDto request) throws Exception {

        return ApiResponse.success(SuccessType.REISSUE_SUCCESS, authService.reissueToken(request, refreshToken));
    }

    @PatchMapping("/log-out") // Spring Security 자체 로그아웃과 충돌하기 때문에 이렇게 써줌
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse logout(Principal principal) {

        authService.logout(JwtProvider.getUserFromPrincial(principal));
        return ApiResponse.success(SuccessType.LOGOUT_SUCCESS);
    }

    @PatchMapping("/sign-out")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse signout(Principal principal) {

        authService.signout(JwtProvider.getUserFromPrincial(principal));
        return ApiResponse.success(SuccessType.SIGNOUT_SUCCESS);
    }

    @PostMapping("/kakao")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse kakaoAccessToken(
            @RequestHeader("Authorization") String code) {

        return ApiResponse.success(SuccessType.KAKAO_ACCESS_TOKEN_SUCCESS, kakaoLoginService.getKakaoAccessToken(code));
    }
}