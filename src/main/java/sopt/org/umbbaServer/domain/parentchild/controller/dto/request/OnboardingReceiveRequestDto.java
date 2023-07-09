package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OnboardingReceiveRequestDto {

    private UserInfoDto userInfo;
}
