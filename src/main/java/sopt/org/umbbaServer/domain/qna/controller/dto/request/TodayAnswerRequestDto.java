package sopt.org.umbbaServer.domain.qna.controller.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TodayAnswerRequestDto {

    @NotBlank // null, "", " "을 모두 허용하지 X
    String answer;

    public static TodayAnswerRequestDto of (String answer) {
        return new TodayAnswerRequestDto(answer);
    }
}
