package sopt.org.umbba.api.controller.parentchild.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.api.controller.user.dto.request.UserInfoDto;
import sopt.org.umbba.domain.domain.parentchild.Parentchild;
import sopt.org.umbba.domain.domain.user.User;

import java.time.LocalTime;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OnboardingInviteResponseDto {

    private Long parentchildId;

    private UserInfoDto userInfo;

    private String parentchildRelation;

    @JsonFormat(pattern = "kk:mm")
    private LocalTime pushTime;

    private String inviteCode;

    public static OnboardingInviteResponseDto of(Parentchild parentchild, User user) {
        return OnboardingInviteResponseDto.builder()
                .parentchildId(parentchild.getId())
                .userInfo(UserInfoDto.of(user))
                .parentchildRelation(parentchild.getRelation().getValue())
                .pushTime(parentchild.getPushTime())
                .inviteCode(parentchild.getInviteCode())
                .build();
    }
}
