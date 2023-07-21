package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OnboardingInviteRequestDto {

    @NotNull
    @Valid
    private UserInfoDto userInfo;

    @JsonProperty("is_invitor_child")
    private Boolean isInvitorChild;

    private String relationInfo; // 아들 or 딸  |  아빠 or 엄마

    @JsonFormat(pattern = "kk:mm")
    private LocalTime pushTime;

    @NotEmpty
    private List<String> onboardingAnswerList;
}
