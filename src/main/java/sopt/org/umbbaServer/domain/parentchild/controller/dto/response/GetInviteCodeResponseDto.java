package sopt.org.umbbaServer.domain.parentchild.controller.dto.response;

import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;

@Getter
@Builder
public class GetInviteCodeResponseDto {

    private String inviteCode;

    public static GetInviteCodeResponseDto of(Parentchild parentchild) {
        return GetInviteCodeResponseDto.builder()
                .inviteCode(parentchild.getInviteCode())
                .build();
    }
}
