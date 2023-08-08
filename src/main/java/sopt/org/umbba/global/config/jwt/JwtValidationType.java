package sopt.org.umbba.global.config.jwt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum JwtValidationType {

    VALID_JWT("유효한 JWT 토큰입니다."),
    INVALID_JWT_SIGNATURE("잘못된 JWT 서명입니다."),
    INVALID_JWT_TOKEN("유효하지 않는 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN("만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN("지원하지 않는 JWT 토큰입니다."),
    EMPTY_JWT("JWT 토큰이 존재하지 않습니다.");

    private final String value;
}
