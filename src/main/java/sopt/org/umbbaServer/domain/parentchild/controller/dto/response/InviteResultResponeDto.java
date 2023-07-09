package sopt.org.umbbaServer.domain.parentchild.controller.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;
import sopt.org.umbbaServer.domain.user.model.User;

import java.util.List;

@Getter
@Builder
public class InviteResultResponeDto {

    Long parentchildId;
    List<User> parentChildUsers;

    public static InviteResultResponeDto of(Parentchild parentchild, List<User> parentChildUsers) {
        return InviteResultResponeDto.builder()
                .parentchildId(parentchild.getId())
                .parentChildUsers(parentChildUsers)
                .build();
    }
}
