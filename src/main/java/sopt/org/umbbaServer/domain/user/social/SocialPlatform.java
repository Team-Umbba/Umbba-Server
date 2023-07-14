package sopt.org.umbbaServer.domain.user.social;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SocialPlatform {
    KAKAO("카카오"),
    APPLE("애플"),
    WITHDRAW("탈퇴한 유저")
    ;

    private final String value;
}