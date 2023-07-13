package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OnboardingInviteRequestDto {

    @NotNull
    private UserInfoDto userInfo;

    @JsonProperty("is_invitor_child")
    private boolean isInvitorChild;

    private String relationInfo; // 아들 or 딸  |  아빠 or 엄마

    @JsonFormat(pattern = "kk:mm")
    private LocalTime pushTime;
}
