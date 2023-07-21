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

    // Common
    REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_WRONG_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 타입이 입력되었습니다."),
    HEADER_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "요청에 필요한 헤더값이 존재하지 않습니다."),
    VALIDATION_WRONG_HTTP_REQUEST(HttpStatus.BAD_REQUEST, "허용되지 않는 문자열이 입력되었습니다."),
    INVALID_HTTP_METHOD(HttpStatus.BAD_REQUEST, "지원되지 않는 HTTP Method 요청입니다."),

    INVALID_SOCIAL_PLATFORM(HttpStatus.BAD_REQUEST, "유효하지 않은 소셜 플랫폼입니다."),
    INVALID_ONBOARDING_ANSWER(HttpStatus.BAD_REQUEST, "유효하지 않은 선택질문 응답값입니다."),
    INVALID_SOCIALPLATFORM(HttpStatus.BAD_REQUEST, "유효하지 않은 소셜 플랫폼 문자열"),

    // ParentChild - Onboarding
    INVALID_PARENT_CHILD_RELATION_INFO(HttpStatus.BAD_REQUEST, "부모자식 관계를 정의할 수 없는 요청값입니다."),
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "유효하지 않는 초대코드입니다."),
    INVALID_PARENT_CHILD_RELATION(HttpStatus.BAD_REQUEST, "유효하지 않는 부모자식 관계입니다."),
    NOT_MATCH_PARENT_CHILD_RELATION(HttpStatus.BAD_REQUEST, "아직 부모자식 관계 매칭이 이루어지지 않았습니다."),
    ALREADY_EXISTS_PARENT_CHILD_USER(HttpStatus.BAD_REQUEST, "이미 해당 유저의 부모자식 관계가 존재합니다."),


    /**
     * 401 UNAUTHORIZED
     */
    INVALID_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 엑세스 토큰입니다."),
    EMPTY_PRINCIPLE_EXCEPTION(HttpStatus.UNAUTHORIZED, "엑세스 토큰이 비어있거나, 유효하지 않은 엑세스 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 엑세스 토큰입니다, 엑세스 토큰을 재발급 받아주세요."),
    INVALID_FIREBASE_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 파이어베이스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다, 다시 로그인을 해주세요."),
    NOT_MATCH_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "일치하지 않는 리프레시 토큰입니다."),
    RECEIVE_AFTER_MATCH(HttpStatus.BAD_REQUEST, "초대코드로 매칭 후에 온보딩 수신이 가능합니다."),


    /**
     * 404 NOT FOUND
     */
    INVALID_USER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    WITHDRAW_USER(HttpStatus.NOT_FOUND, "탈퇴한 회원입니다."),
    NOT_EXIST_PARENT_CHILD_USER(HttpStatus.NOT_FOUND, "해당 부모자식 관계에 해당하는 유저가 존재하지 않습니다."),
    NOT_EXIST_CHILD_USER(HttpStatus.NOT_FOUND, "해당 관계에 속하는 자식 유저가 존재하지 않습니다."),
    NOT_FOUND_QNA(HttpStatus.NOT_FOUND, "해당 아이디와 일치하는 QnA 데이터가 없습니다."),
    USER_HAVE_NO_PARENTCHILD(HttpStatus.NOT_FOUND, "회원이 속한 부모자식 관계가 없습니다."),
    NOT_EXIST_PARENT_CHILD_RELATION(HttpStatus.NOT_FOUND, "존재하지 않는 부모자식 관계입니다."),
    USER_HAVE_NO_QNALIST(HttpStatus.NOT_FOUND, "해당 유저가 가지고 있는 QnA 데이터가 없습니다."),
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
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 관련 에러가 발생했습니다."),
    FIREBASE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파이어베이스 서버와의 연결에 실패했습니다."),
    FAIL_TO_SEND_PUSH_ALARM(HttpStatus.INTERNAL_SERVER_ERROR, "푸시 알림 메세지 전송에 실패했습니다."),


    // ETC
    INDEX_OUT_OF_BOUNDS(HttpStatus.INTERNAL_SERVER_ERROR, "인덱스 범위를 초과했습니다."),
    JWT_SERIALIZE(HttpStatus.INTERNAL_SERVER_ERROR, "JWT 라이브러리 직렬화에 실패했습니다."),
    OPTIONAL_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, "Optional 객체가 비어있습니다."),
    NON_UNIQUE_RESULT_OF_QUERY(HttpStatus.INTERNAL_SERVER_ERROR, "JPA 쿼리가 유일한 결과를 반환하지 않습니다."),
    INTERNAL_SERVLET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "디스패처 서블릿이 요청을 처리하는 중에 예외가 발생했습니다."),
    NO_ENUM_TYPE(HttpStatus.INTERNAL_SERVER_ERROR, "해당 Enum 타입이 데이터베이스 엔티티와 매핑될 수 없습니다."),
    DATA_INTEGRITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 무결성 제약조건을 위반했습니다."),
    NULL_POINTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NULL 포인터를 참조했습니다."),



    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
