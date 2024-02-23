package sopt.org.umbba.common.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access =  AccessLevel.PRIVATE)
public enum SuccessType {

    /**
     * 200 OK
     */
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    REISSUE_SUCCESS(HttpStatus.OK, "Access 토큰 재발급에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),
    SIGNOUT_SUCCESS(HttpStatus.OK, "회원탈퇴에 성공했습니다."),
    KAKAO_ACCESS_TOKEN_SUCCESS(HttpStatus.OK, "카카오 엑세스 토큰을 가져오는데 성공했습니다"),
    GET_TODAY_QNA_SUCCESS(HttpStatus.OK, "일일문답 조회에 성공했습니다."),
    GET_QNA_LIST_SUCCESS(HttpStatus.OK, "섹션별 과거의 문답 리스트 조회에 성공했습니다."),
    GET_SINGLE_QNA_SUCCESS(HttpStatus.OK, "과거의 문답 개별 조회에 성공했습니다."),
    GET_MAIN_HOME_SUCCESS(HttpStatus.OK, "메인 홈 화면 정보 불러오기에 성공했습니다."),
    GET_INVITE_CODE_SUCCESS(HttpStatus.OK, "초대장을 보낼 코드 조회에 성공했습니다."),
    PUSH_ALARM_SUCCESS(HttpStatus.OK, "푸시알림 전송에 성공했습니다."),
    PUSH_ALARM_PERIODIC_SUCCESS(HttpStatus.OK, "오늘의 질문 푸시알림 활성에 성공했습니다."),
    REMIND_QUESTION_SUCCESS(HttpStatus.OK, "상대방에게 질문을 리마인드 하는 데 성공했습니다."),
    GET_MY_USER_INFO_SUCCESS(HttpStatus.OK, "마이페이지 내 정보 조회에 성공했습니다."),
    TEST_SUCCESS(HttpStatus.OK, "데모데이 테스트용 API 호출에 성공했습니다."),
    RESTART_QNA_SUCCESS(HttpStatus.OK, "7일 이후 문답이 정상적으로 시작되었습니다."),
    GET_USER_FIRST_ENTRY_SUCCESS(HttpStatus.OK, "7일 이후 문답이 정상적으로 시작되었습니다."),


    /**
     * 201 CREATED
     */
    CREATE_PARENT_CHILD_SUCCESS(HttpStatus.CREATED, "온보딩 정보를 입력받아 부모자식 관계를 생성하는 데 성공했습니다."),
    MATCH_PARENT_CHILD_SUCCESS(HttpStatus.CREATED, "부모자식 관계 매칭에 성공했습니다."),
    ANSWER_TODAY_QUESTION_SUCCESS(HttpStatus.CREATED, "오늘의 일일문답에 답변을 완료하였습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }
}
