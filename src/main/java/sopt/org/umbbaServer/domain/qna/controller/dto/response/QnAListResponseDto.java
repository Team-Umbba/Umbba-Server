package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QnAListResponseDto {

    private int index;
    private String question;
}
