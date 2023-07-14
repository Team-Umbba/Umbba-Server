package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.qna.model.QnA;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetMainViewResponseDto {

    private String section;
    private String topic;
    private Integer count;

    public static GetMainViewResponseDto of (QnA qnA, int count) {
        return GetMainViewResponseDto.builder()
                .section(qnA.getQuestion().getSection().getValue())
                .topic(qnA.getQuestion().getTopic())
                .count(count)
                .build();
    }
}
