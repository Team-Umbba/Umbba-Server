package sopt.org.umbba.api.controller.closer.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.domain.closer.CloserQnA;
import sopt.org.umbba.domain.domain.closer.CloserQuestion;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TodayCloserQnAResponseDto {

    private Long closerQnaId;

    private int responseCase;

    private String balanceQuestion;
    private String choiceAnswer1;
    private String choiceAnswer2;

    private String myChoice;
    private String opponentChoice;

    public static TodayCloserQnAResponseDto of(CloserQnA closerQna, int responseCase, boolean isMeChild) {

        CloserQuestion closerQuestion = closerQna.getCloserQuestion();
        int myAnswer;
        int opponentAnswer;
        if (isMeChild) {
            myAnswer = closerQna.getChildAnswer();
            opponentAnswer = closerQna.getParentAnswer();
        } else {
            myAnswer = closerQna.getParentAnswer();
            opponentAnswer = closerQna.getChildAnswer();
        }

        String myChoice;
        if (myAnswer == 0) {
            myChoice = null;
        } else if (myAnswer == 1) {
            myChoice = closerQuestion.getChoiceAnswer1();
        } else {
            myChoice = closerQuestion.getChoiceAnswer2();
        }
        String opponentChoice;
        if (opponentAnswer == 0) {
            opponentChoice = null;
        } else if (opponentAnswer == 1) {
            opponentChoice = closerQuestion.getChoiceAnswer1();
        } else {
            opponentChoice = closerQuestion.getChoiceAnswer2();
        }

        if (responseCase == 3 && (myAnswer != opponentAnswer)) {
            responseCase = 4;
        }

        return TodayCloserQnAResponseDto.builder()
                .closerQnaId(closerQna.getId())
                .responseCase(responseCase)
                .balanceQuestion(closerQuestion.getBalanceQuestion())
                .choiceAnswer1(closerQuestion.getChoiceAnswer1())
                .choiceAnswer2(closerQuestion.getChoiceAnswer2())
                .myChoice(myChoice)
                .opponentChoice(opponentChoice)
                .build();
    }
}
