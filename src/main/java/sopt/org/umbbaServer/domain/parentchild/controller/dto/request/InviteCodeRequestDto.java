package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InviteCodeRequestDto {

    @NotBlank(message = "초대코드는 필수 입력 값입니다.")
    @Size(max = 11)
    private String inviteCode;
}
