package sopt.org.umbbaServer.domain.qna.controller.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TodayAnswerRequestDto {

    @NotBlank // null, "", " "을 모두 허용하지 X
    String answer;
}
