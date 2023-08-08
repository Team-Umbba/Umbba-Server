package sopt.org.umbba.api.controller.parentchild.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.umbba.api.controller.user.dto.request.UserInfoDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OnboardingReceiveRequestDto {

    @NotNull
    @Valid
    private UserInfoDto userInfo;

    @NotEmpty
    private List<String> onboardingAnswerList;
}
