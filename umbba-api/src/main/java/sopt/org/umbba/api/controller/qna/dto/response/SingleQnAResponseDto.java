package sopt.org.umbba.api.controller.qna.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.domain.qna.QnA;
import sopt.org.umbba.domain.domain.qna.Question;
import sopt.org.umbba.domain.domain.user.User;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SingleQnAResponseDto {

    private Long qnaId;
    private Integer index;

    private String section;
    private String topic;

    private String opponentQuestion;
    private String myQuestion;

    private String opponentAnswer;
    private String myAnswer;

    private String opponentUsername;
    private String myUsername;

    public static SingleQnAResponseDto of(User myUser, User opponentUser, int index, QnA todayQnA, Question todayQuestion) {
        String opponentQuestion;
        String myQuestion;
        String opponentAnswer;
        String myAnswer;

        if (myUser.isMeChild()) {
            opponentQuestion = todayQuestion.getParentQuestion();
            myQuestion = todayQuestion.getChildQuestion();
            opponentAnswer = todayQnA.getParentAnswer();
            myAnswer = todayQnA.getChildAnswer();
        } else {
            opponentQuestion = todayQuestion.getChildQuestion();
            myQuestion = todayQuestion.getParentQuestion();
            opponentAnswer = todayQnA.getChildAnswer();
            myAnswer = todayQnA.getParentAnswer();
        }

        return SingleQnAResponseDto.builder()
                .qnaId(todayQnA.getId())
                .index(index)
                .section(todayQuestion.getSection().getValue())
                .topic(todayQuestion.getTopic())
                .opponentQuestion(opponentQuestion)
                .myQuestion(myQuestion)
                .opponentAnswer(opponentAnswer)
                .myAnswer(myAnswer)
                .opponentUsername(opponentUser.getUsername())
                .myUsername(myUser.getUsername())
                .build();
    }
}
