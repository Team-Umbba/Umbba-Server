package sopt.org.umbbaServer.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbbaServer.domain.user.controller.dto.request.SocialLoginRequestDto;
import sopt.org.umbbaServer.domain.user.controller.dto.response.UserLoginResponseDto;
import sopt.org.umbbaServer.global.config.jwt.JwtProvider;
import sopt.org.umbbaServer.global.config.jwt.TokenDto;
import sopt.org.umbbaServer.global.config.auth.UserAuthentication;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.repository.UserRepository;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;
import sopt.org.umbbaServer.domain.user.social.apple.AppleLoginService;
import sopt.org.umbbaServer.domain.user.social.kakao.KakaoLoginService;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService { 

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    private final AppleLoginService appleLoginService;
    private final KakaoLoginService kakaoLoginService;

    @Transactional
    public UserLoginResponseDto login(String socialAccessToken, SocialLoginRequestDto request) throws NoSuchAlgorithmException, InvalidKeySpecException {

        SocialPlatform socialPlatform = request.getSocialPlatform();
        String socialId = login(request.getSocialPlatform(), socialAccessToken);

        boolean isRegistered = isUserBySocialAndSocialId(socialPlatform, socialId);

        if (!isRegistered) {

            User user = User.builder()
                    .socialPlatform(socialPlatform)
                    .socialId(socialId).build();

            userRepository.save(user);
        }

        User loginUser = getUserBySocialAndSocialId(socialPlatform, socialId);

        // 카카오는 정보 더 많이 받아올 수 있으므로 추가 설정
        if (socialPlatform == SocialPlatform.KAKAO) {
            kakaoLoginService.setKakaoInfo(loginUser, socialAccessToken);
        }

        TokenDto tokenDto = generateToken(new UserAuthentication(loginUser.getId(), null, null));
        loginUser.updateRefreshToken(tokenDto.getRefreshToken());

        return UserLoginResponseDto.of(loginUser, tokenDto.getAccessToken());
    }

    @Transactional
    public TokenDto reissueToken(Long userId, String refreshToken) throws Exception {

        User user = getUserById(userId); //userId가 잘못 날라오는 경우에 대비해 남김

        if (jwtProvider.validRefreshToken(userId, refreshToken)) {
            return generateToken(new UserAuthentication(userId, null, null));

        } else {
            throw new CustomException(ErrorType.NOT_MATCH_REFRESH_TOKEN);
        }
    }

    @Transactional
    public void logout(Long userId) {
        User user = getUserById(userId);
        user.updateRefreshToken(null);
        jwtProvider.deleteRefreshToken(userId);
    }

    private TokenDto generateToken(Authentication authentication) {
        return TokenDto.of(
                jwtProvider.generateAccessToken(authentication),
                jwtProvider.generateRefreshToken(authentication));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorType.INVALID_USER));
    }

    private User getUserBySocialAndSocialId(SocialPlatform socialPlatform, String socialId) {
        return userRepository.findBySocialPlatformAndSocialId(socialPlatform, socialId)
                .orElseThrow(() -> new CustomException(ErrorType.INVALID_USER));
    }

    private boolean isUserBySocialAndSocialId(SocialPlatform socialPlatform, String socialId) {
        return userRepository.existsBySocialPlatformAndSocialId(socialPlatform, socialId);
    }

    private String login(SocialPlatform socialPlatform, String socialAccessToken) throws NoSuchAlgorithmException, InvalidKeySpecException {
        switch (socialPlatform.toString()) {
            case "APPLE":
                return appleLoginService.getAppleId(socialAccessToken);
            case "KAKAO":
                return kakaoLoginService.getKakaoId(socialAccessToken);
            default:
                throw new CustomException(ErrorType.INVALID_SOCIAL_ACCESS_TOKEN);
        }

    }
}
