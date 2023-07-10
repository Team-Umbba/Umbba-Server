package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.qna.model.Question;
import sopt.org.umbbaServer.domain.user.model.User;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TodayQnAResponseDto {

    private String section;
    private String topic;
    private String opponentQuestion;
    private String myQuestion;

    private String opponentAnswer;
    private String myAnswer;

    @JsonProperty("isOpponentAnswer")
    private boolean isOpponentAnswer;

    @JsonProperty("isMyAnswer")
    private boolean isMyAnswer;

    private String opponentUsername;
    private String myUsername;

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

}
