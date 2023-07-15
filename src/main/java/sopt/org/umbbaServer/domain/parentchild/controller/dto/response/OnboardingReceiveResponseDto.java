package sopt.org.umbbaServer.domain.parentchild.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;
import sopt.org.umbbaServer.domain.user.model.User;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OnboardingReceiveResponseDto {

    private UserInfoDto userInfo;

    private InviteResultResponseDto parentchildInfo;

    @JsonFormat(pattern = "kk:mm")
    private LocalTime pushTime;

    public static OnboardingReceiveResponseDto of(Parentchild parentchild, User user, List<User> parentChildUsers) {
        return OnboardingReceiveResponseDto.builder()
                .userInfo(UserInfoDto.of(user))
                .parentchildInfo(InviteResultResponseDto.of(parentchild, parentChildUsers))
                .pushTime(parentchild.getPushTime())
                .build();
    }
}
