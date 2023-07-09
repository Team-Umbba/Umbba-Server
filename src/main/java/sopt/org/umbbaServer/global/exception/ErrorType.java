package sopt.org.umbbaServer.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorType {

    /**
     * 400 BAD REQUEST
     */
    REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    VALIDATION_WRONG_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 타입이 입력되었습니다"),
    EMPTY_PRINCIPLE_EXCEPTION(HttpStatus.BAD_REQUEST, "Principle 객체가 없습니다. (null)"),
    HEADER_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "요청에 필요한 헤더값이 존재하지 않습니다."),


    /**
     * 401 UNAUTHORIZED
     */
    INVALID_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 엑세스 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다, 엑세스 토큰을 재발급 받아주세요."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다, 다시 로그인을 해주세요."),
    NOT_MATCH_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "일치하지 않는 리프레시 토큰입니다."),

    /**
     * 404 NOT FOUND
     */
    INVALID_USER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    USER_HAVE_NO_PARENTCHILD(HttpStatus.NOT_FOUND, "회원이 속한 부모자식 관계가 없습니다."),
    PARENTCHILD_HAVE_NO_QNALIST(HttpStatus.NOT_FOUND, "부모자식 관계가 가지고 있는 QnA 데이터가 없습니다."),
    PARENTCHILD_HAVE_NO_OPPONENT(HttpStatus.NOT_FOUND, "부모자식 관계에 1명만 참여하고 있습니다."),

    /**
     * About Apple (HttpStatus 고민)
     */
    INVALID_APPLE_PUBLIC_KEY(HttpStatus.BAD_REQUEST, "Apple JWT 값의 alg, kid 정보가 올바르지 않습니다."),
    INVALID_APPLE_IDENTITY_TOKEN(HttpStatus.BAD_REQUEST, "Apple OAuth Identity Token 형식이 올바르지 않습니다."),
    EXPIRED_APPLE_IDENTITY_TOKEN(HttpStatus.BAD_REQUEST, "Apple OAuth 로그인 중 Identity Token 유효기간이 만료됐습니다."),
    INVALID_APPLE_CLAIMS(HttpStatus.BAD_REQUEST, "Apple OAuth Claims 값이 올바르지 않습니다."),
    INVALID_ENCRYPT_COMMUNICATION(HttpStatus.BAD_REQUEST, "Apple OAuth 통신 암호화 과정 중 문제가 발생했습니다."),
    CREATE_PUBLIC_KEY_EXCEPTION(HttpStatus.BAD_REQUEST, "Apple OAuth 로그인 중 public verify 생성에 문제가 발생했습니다."),

    /**
     * 500 INTERNAL SERVER ERROR
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다"),


    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
