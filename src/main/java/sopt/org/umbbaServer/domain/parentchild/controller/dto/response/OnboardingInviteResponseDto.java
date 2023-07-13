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

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OnboardingInviteResponseDto {

    private Long parentchildId;

    private UserInfoDto userInfo;

    private String parentchildRelation;

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
