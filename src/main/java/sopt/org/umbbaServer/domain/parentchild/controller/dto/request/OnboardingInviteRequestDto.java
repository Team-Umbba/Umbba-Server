package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OnboardingInviteRequestDto {

    @NotNull
    private UserInfoDto userInfo;

    // TODO 아래 두 필드에 따라 부모자식 관계 케이스 분류하여 저장
    private boolean isInvitorChild;

    private String relationInfo; // 아들 or 딸  |  아빠 or 엄마

    private LocalTime pushTime;
}
