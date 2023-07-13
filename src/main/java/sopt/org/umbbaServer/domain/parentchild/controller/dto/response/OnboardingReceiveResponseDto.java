package sopt.org.umbbaServer.domain.parentchild.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;
import sopt.org.umbbaServer.domain.user.model.User;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OnboardingReceiveResponseDto {

    private UserInfoDto userInfo;

    private InviteResultResponeDto parentchildInfo;

    private LocalTime pushTime;

    public static OnboardingReceiveResponseDto of(Parentchild parentchild, User user, List<User> parentChildUsers) {
        return OnboardingReceiveResponseDto.builder()
                .userInfo(UserInfoDto.of(user))
                .parentchildInfo(InviteResultResponeDto.of(parentchild, parentChildUsers))
                .pushTime(parentchild.getPushTime())
                .build();
    }
}
