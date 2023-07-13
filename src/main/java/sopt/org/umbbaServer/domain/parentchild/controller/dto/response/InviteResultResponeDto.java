package sopt.org.umbbaServer.domain.parentchild.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
public class InviteResultResponeDto {

    private Long parentchildId;
    private List<UserInfoDto> parentchildUsers;
    private String parentchildRelation;

    public static InviteResultResponeDto of(Parentchild parentchild, List<User> parentChildUsers) {
        return InviteResultResponeDto.builder()
                .parentchildId(parentchild.getId())
                .parentchildUsers(parentChildUsers.stream().map(u -> UserInfoDto.of(u)).collect(Collectors.toList()))
                .parentchildRelation(parentchild.getRelation().getValue())
                .build();
    }
}
