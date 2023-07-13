package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.qna.model.QnA;
import sopt.org.umbbaServer.domain.qna.model.Question;
import sopt.org.umbbaServer.domain.user.model.User;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SingleQnAResponseDto {

    private Long qnaId;
    private String section;
    private String topic;

    private String opponentQuestion;
    private String myQuestion;

    private String opponentAnswer;
    private String myAnswer;

    private String opponentUsername;
    private String myUsername;

    public static SingleQnAResponseDto of(User myUser, User opponentUser, QnA todayQnA, Question todayQuestion, boolean isMeChild) {
        String opponentQuestion;
        String myQuestion;
        String opponentAnswer;
        String myAnswer;

        if (isMeChild) {
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
