package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OnboardingReceiveRequestDto {

    private Long parentChildId;

    @NotNull
    private UserInfoDto userInfo;

    // TODO 선택질문에 대한 답변 필드 추가 필요
}
