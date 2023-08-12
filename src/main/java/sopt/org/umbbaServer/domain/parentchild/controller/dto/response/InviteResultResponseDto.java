package sopt.org.umbbaServer.domain.parentchild.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;
import sopt.org.umbbaServer.domain.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InviteResultResponseDto {

    private Long parentchildId;

    private Boolean isMatchFinish;

    private List<UserInfoDto> parentchildUsers;
    private String parentchildRelation;

    public static InviteResultResponseDto of(boolean isMatchFinish, Parentchild parentchild, List<User> parentChildUsers) {
        return InviteResultResponseDto.builder()
                .parentchildId(parentchild.getId())
                .isMatchFinish(isMatchFinish)
                .parentchildUsers(parentChildUsers.stream().map(u -> UserInfoDto.of(u)).collect(Collectors.toList()))
                .parentchildRelation(parentchild.getRelation().getValue())
                .build();
    }

    public static InviteResultResponseDto of(Parentchild parentchild, List<User> parentChildUsers) {
        return InviteResultResponseDto.builder()
                .parentchildId(parentchild.getId())
                .parentchildUsers(parentChildUsers.stream().map(u -> UserInfoDto.of(u)).collect(Collectors.toList()))
                .parentchildRelation(parentchild.getRelation().getValue())
                .build();
    }
}