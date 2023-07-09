package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OnboardingInviteRequestDto {

    @NotNull
    private UserInfoDto userInfo;

    private boolean isInvitorChild;

    private String relationInfo; // 아들 or 딸  |  아빠 or 엄마

    // TODO 디폴트 값 지정해줘야 할까? -> by 디폴트 생성자
    @JsonFormat(pattern = "kk:mm")
    private LocalTime pushTime;
}
