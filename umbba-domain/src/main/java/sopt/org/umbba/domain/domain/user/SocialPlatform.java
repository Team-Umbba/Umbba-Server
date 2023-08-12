package sopt.org.umbba.domain.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;

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
            if (platform.toString().equals(value)) {
                return platform;
            }
        }
        throw new CustomException(ErrorType.INVALID_SOCIALPLATFORM);
    }
}