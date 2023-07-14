package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.qna.model.Question;
import sopt.org.umbbaServer.domain.user.model.User;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TodayQnAResponseDto {

    private int responseCase;  // case를 1,2,3으로 구분 (Client)

    private Long qnaId;
    private String section;
    private String topic;
    private String opponentQuestion;
    private String myQuestion;

    private String opponentAnswer;
    private String myAnswer;

    private Boolean isOpponentAnswer;
    private Boolean isMyAnswer;

    private String opponentUsername;
    private String myUsername;

    // 예외상황에 따른 필드
    private String inviteCode;
    private String inviteUsername;
    private String installUrl;  // TODO Firebase Dynamic Link

    private Boolean relativeUserActive;


    // 1. 오늘의 질문을 조회한 일반적인 경우
    public static TodayQnAResponseDto of(User myUser, User opponentUser, QnA todayQnA, Question todayQuestion, boolean isMeChild) {
        String opponentQuestion;
        String myQuestion;
        String opponentAnswer;
        String myAnswer;
        boolean isOpponentAnswer;
        boolean isMyAnswer;

        if (isMeChild) {
            opponentQuestion = todayQuestion.getParentQuestion();
            myQuestion = todayQuestion.getChildQuestion();
            opponentAnswer = todayQnA.getParentAnswer();
            myAnswer = todayQnA.getChildAnswer();
            isOpponentAnswer = todayQnA.isParentAnswer();
            isMyAnswer = todayQnA.isChildAnswer();
        } else {
            opponentQuestion = todayQuestion.getChildQuestion();
            myQuestion = todayQuestion.getParentQuestion();
            opponentAnswer = todayQnA.getChildAnswer();
            myAnswer = todayQnA.getParentAnswer();
            isOpponentAnswer = todayQnA.isChildAnswer();
            isMyAnswer = todayQnA.isParentAnswer();
        }

        return TodayQnAResponseDto.builder()
                .responseCase(1)
                .qnaId(todayQnA.getId())
                .section(todayQuestion.getSection().getValue())
                .topic(todayQuestion.getTopic())
                .opponentQuestion(opponentQuestion)
                .myQuestion(myQuestion)
                .opponentAnswer(opponentAnswer)
                .myAnswer(myAnswer)
                .isOpponentAnswer(isOpponentAnswer)
                .isMyAnswer(isMyAnswer)
                .opponentUsername(opponentUser.getUsername())
                .myUsername(myUser.getUsername())
                .build();
    }

    // 2. 아직 부모자식 관계가 매칭되지 않은 경우
    public static TodayQnAResponseDto of (String inviteCode, String inviteUsername, String installUrl) {
        return TodayQnAResponseDto.builder()
                .responseCase(2)
                .inviteCode(inviteCode)
                .inviteUsername(inviteUsername)
                .installUrl(installUrl)
                .build();
    }

    // 3. 부모자식 중 상대 측 유저가 탈퇴한 경우
    public static TodayQnAResponseDto of (boolean relativeUserActive) {
        return TodayQnAResponseDto.builder()
                .responseCase(3)
                .relativeUserActive(relativeUserActive)
                .build();
    }
}


