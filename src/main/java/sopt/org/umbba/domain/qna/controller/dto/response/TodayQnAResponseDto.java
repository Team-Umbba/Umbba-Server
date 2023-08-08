package sopt.org.umbba.domain.qna.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.qna.model.QnA;
import sopt.org.umbba.domain.qna.model.Question;
import sopt.org.umbba.domain.user.model.User;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TodayQnAResponseDto {

    private Long qnaId;
    private Integer index;
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


    public static TodayQnAResponseDto of(User myUser, User opponentUser, int count, QnA todayQnA, Question todayQuestion) {
        String opponentQuestion;
        String myQuestion;
        String opponentAnswer;
        String myAnswer;
        boolean isOpponentAnswer;
        boolean isMyAnswer;

        if (myUser.isMeChild()) {
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
                .qnaId(todayQnA.getId())
                .index(count)
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


