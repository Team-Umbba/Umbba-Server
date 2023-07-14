package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.umbbaServer.domain.parentchild.model.OnboardingAnswer;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OnboardingReceiveRequestDto {

    @NotBlank(message = "부모자식 관계 아이디는 필수 입력 값입니다.")
    private Long parentChildId;

    @NotNull
    @Valid
    private UserInfoDto userInfo;

    private List<OnboardingAnswer> onboardingAnswerList;
}
