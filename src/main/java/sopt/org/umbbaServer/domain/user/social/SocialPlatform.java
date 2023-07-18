package sopt.org.umbbaServer.domain.user.social;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.umbbaServer.domain.qna.model.OnboardingAnswer;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SocialPlatform {
    KAKAO("카카오"),
    APPLE("애플"),
    WITHDRAW("탈퇴한 유저")
    ;

    private final String value;

    public static SocialPlatform of(String value) {
        for (SocialPlatform platform : SocialPlatform.values()) {
            if (platform.getValue().equals(value)) {
                return platform;
            }
        }
        throw new CustomException(ErrorType.INVALID_ONBOARDING_ANSWER);
    }
}