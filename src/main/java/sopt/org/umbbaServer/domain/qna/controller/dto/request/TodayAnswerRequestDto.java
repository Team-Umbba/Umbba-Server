package sopt.org.umbbaServer.domain.qna.controller.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TodayAnswerRequestDto {

    @NotBlank // null, "", " "을 모두 허용하지 X
    String answer;
}
