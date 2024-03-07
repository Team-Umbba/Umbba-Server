package sopt.org.umbba.api.controller.closer.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TodayCloserAnswerRequestDto {

    @Min(value = 1, message = "답변은 1 혹은 2여야 합니다.")
    @Max(value = 2, message = "답변은 1 혹은 2여야 합니다.")
    int answer;

    public static TodayCloserAnswerRequestDto of (int answer) {
        return new TodayCloserAnswerRequestDto(answer);
    }
}
