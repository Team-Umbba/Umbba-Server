package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.umbbaServer.domain.qna.model.QuestionEffect;
import sopt.org.umbbaServer.domain.qna.model.QuestionSection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayQnAResponseDto {

    private QuestionSection section;
    private QuestionEffect effect;
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
}
