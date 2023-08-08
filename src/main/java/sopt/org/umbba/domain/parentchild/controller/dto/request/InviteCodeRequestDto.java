package sopt.org.umbba.domain.parentchild.controller.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InviteCodeRequestDto {

    @NotBlank(message = "초대코드는 필수 입력 값입니다.")
    @Pattern(regexp = "[A-Z]{4}-[A-Za-z0-9]{6}", message = "초대코드 형식에 맞지 않습니다.")
    @Size(max = 11)
    private String inviteCode;
}
