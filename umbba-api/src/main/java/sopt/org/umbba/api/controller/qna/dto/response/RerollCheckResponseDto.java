package sopt.org.umbba.api.controller.qna.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.domain.domain.qna.QnA;
import sopt.org.umbba.domain.domain.qna.Question;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RerollCheckResponseDto {

    private Long questionId;
    private String childQuestion;
    private String parentQuestion;
    private String section;
    private String topic;

    public static RerollCheckResponseDto of(Question question) {
        return RerollCheckResponseDto.builder()
            .questionId(question.getId())
            .childQuestion(question.getChildQuestion())
            .parentQuestion(question.getParentQuestion())
            .section(question.getSection().getValue())
            .topic(question.getTopic())
            .build();
    }
}
