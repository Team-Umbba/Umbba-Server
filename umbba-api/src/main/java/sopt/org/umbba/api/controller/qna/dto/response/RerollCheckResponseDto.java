package sopt.org.umbba.api.controller.qna.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.domain.qna.Question;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RerollCheckResponseDto {

    private Long questionId;
    private String newQuestion;

    public static RerollCheckResponseDto of(boolean isMeChild, Question question) {

        String newQuestion;
        if (isMeChild) {
            newQuestion = question.getChildQuestion();
        } else {
            newQuestion = question.getParentQuestion();
        }

        return RerollCheckResponseDto.builder()
            .questionId(question.getId())
            .newQuestion(newQuestion)
            .build();
    }
}
