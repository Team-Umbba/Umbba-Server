package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InviteCodeRequestDto {

    @NotBlank(message = "초대코드는 필수 입력 값입니다.")
    @Size(max = 11)
    private String inviteCode;
}
