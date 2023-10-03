package sopt.org.umbba.api.service.user;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbba.api.config.auth.UserAuthentication;
import sopt.org.umbba.api.config.jwt.JwtProvider;
import sopt.org.umbba.api.config.jwt.TokenDto;
import sopt.org.umbba.api.controller.user.dto.request.RefreshRequestDto;
import sopt.org.umbba.api.controller.user.dto.request.SocialLoginRequestDto;
import sopt.org.umbba.api.controller.user.dto.response.UserLoginResponseDto;
import sopt.org.umbba.api.service.user.social.apple.AppleLoginService;
import sopt.org.umbba.api.service.user.social.kakao.KakaoLoginService;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.parentchild.repository.ParentchildRepository;
import sopt.org.umbba.domain.domain.qna.repository.QnARepository;
import sopt.org.umbba.domain.domain.user.SocialPlatform;
import sopt.org.umbba.domain.domain.user.User;
import sopt.org.umbba.domain.domain.user.repository.UserRepository;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final ParentchildRepository parentchildRepository;
    private final QnARepository qnARepository;

    private final AppleLoginService appleLoginService;
    private final KakaoLoginService kakaoLoginService;

    @Transactional
    public UserLoginResponseDto login(String socialAccessToken, SocialLoginRequestDto request) throws NoSuchAlgorithmException, InvalidKeySpecException {

        SocialPlatform socialPlatform = SocialPlatform.of(request.getSocialPlatform());
        String socialId = login(socialPlatform, socialAccessToken);

        boolean isRegistered = isUserBySocialAndSocialId(socialPlatform, socialId);

        if (!isRegistered) {

            User user = User.builder()
                    .socialPlatform(socialPlatform)
                    .socialId(socialId)
                    .isMeChild(true)
                    .isMatchFinish(false)
                    .fcmToken(request.getFcmToken())
                    .build();

            userRepository.save(user);
        }

        User loginUser = getUserBySocialAndSocialId(socialPlatform, socialId);

        // 카카오는 정보 더 많이 받아올 수 있으므로 추가 설정
        if (socialPlatform == SocialPlatform.KAKAO) {
            kakaoLoginService.setKakaoInfo(loginUser, socialAccessToken);
        }

        TokenDto tokenDto = jwtProvider.issueToken(new UserAuthentication(loginUser.getId(), null, null));
        loginUser.updateRefreshToken(tokenDto.getRefreshToken());

        // 클라이언트 요청에 따라 FCM 토큰을 로그인할 때마다 업데이트 하도록 변경
        loginUser.updateFcmToken(request.getFcmToken());
        log.info("🔮{}의 JWT Access Token: {}", loginUser.getUsername(), tokenDto.getAccessToken());

        return UserLoginResponseDto.of(loginUser, tokenDto.getAccessToken());
    }

    @Transactional
    public TokenDto reissueToken(RefreshRequestDto request, String refreshToken) throws Exception {

        Long userId = request.getUserId();
        User user = getUserById(userId);  // userId가 DB에 저장된 유효한 값인지 검사

        if (!jwtProvider.validateRefreshToken(request.getUserId(), refreshToken)) {
            throw new CustomException(ErrorType.NOT_MATCH_REFRESH_TOKEN);
        }

        TokenDto reissuedToken =  jwtProvider.issueToken(new UserAuthentication(userId, null, null));
        user.updateRefreshToken(reissuedToken.getRefreshToken());
        return reissuedToken;
    }

    @Transactional
    public void logout(Long userId) {
        User user = getUserById(userId);
        user.updateRefreshToken(null);
        jwtProvider.deleteRefreshToken(userId);
    }

    @Transactional
    public void signout(Long userId) {
        User user = getUserById(userId);
        user.updateRefreshToken(null);
        user.updateFcmToken(null);
        user.deleteSocialInfo();
        jwtProvider.deleteRefreshToken(userId); // 일치하는 ID가 없는 경우에는 아무 동작도 수행하지 않음 (CrudRepository 기본 동작)

        Parentchild parentChild = user.getParentChild();
        List<User> findUsers = userRepository.findUserByParentChild(parentChild);

        boolean allUsersDeleted = true;
        for (User findUser : findUsers) {
            if (!(findUser.getSocialPlatform().equals(SocialPlatform.WITHDRAW))){
                allUsersDeleted = false;
                break;
            }
        }
        if (allUsersDeleted) {
            findUsers.forEach(u -> userRepository.deleteById(u.getId()));
            parentchildRepository.deleteById(parentChild.getId());
            parentChild.getQnaList().forEach(qna -> qnARepository.deleteById(qna.getId()));
        }
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

    private String login(SocialPlatform socialPlatform, String socialAccessToken) {

            try {
                switch (socialPlatform.toString()) {
                    case "APPLE":
                        return appleLoginService.getAppleId(socialAccessToken);
                    case "KAKAO":
                        return kakaoLoginService.getKakaoId(socialAccessToken);
                    default:
                        throw new CustomException(ErrorType.INVALID_SOCIAL_PLATFORM);
                }
            } catch (FeignException e) {
                throw new CustomException(ErrorType.INVALID_SOCIAL_ACCESS_TOKEN);
            }
    }
}