package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.qna.model.QnA;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMainViewResponseDto {

    private String section;
    private String topic;
    private Integer index;
    private String inviteCode;

    public static GetMainViewResponseDto of (QnA qnA, int index) {
        return GetMainViewResponseDto.builder()
                .section(qnA.getQuestion().getSection().getValue())
                .topic(qnA.getQuestion().getTopic())
                .index(index)
                .build();
    }

    public static GetMainViewResponseDto of (String inviteCode) {
        return GetMainViewResponseDto.builder()
                .inviteCode(inviteCode)
                .build();
    }
}
