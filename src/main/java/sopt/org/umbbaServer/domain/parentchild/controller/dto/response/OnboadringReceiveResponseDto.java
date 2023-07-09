package sopt.org.umbbaServer.domain.parentchild.controller.dto.response;

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
public class OnboadringReceiveResponseDto {

    private UserInfoDto userInfo;

    private InviteResultResponeDto parentchildInfo;

    private LocalTime pushTime;

    public static OnboadringReceiveResponseDto of(Parentchild parentchild, User user, List<User> parentChildUsers) {
        return OnboadringReceiveResponseDto.builder()
                .userInfo(UserInfoDto.of(user))
                .parentchildInfo(InviteResultResponeDto.of(parentchild, parentChildUsers))
                .pushTime(parentchild.getPushTime())
                .build();
    }
}
