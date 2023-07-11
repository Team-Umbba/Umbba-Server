package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.qna.model.QnA;

@Getter
@Builder
public class GetMainViewResponseDto {

    private String section;
    private String topic;

    public static GetMainViewResponseDto of (QnA qnA) {
        return GetMainViewResponseDto.builder()
                .section(qnA.getQuestion().getSection().getValue())
                .topic(qnA.getQuestion().getTopic())
                .build();
    }
}
