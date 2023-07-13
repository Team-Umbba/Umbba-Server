package sopt.org.umbbaServer.domain.parentchild.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.parentchild.model.Parentchild;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetInviteCodeResponseDto {

    private String inviteCode;

    public static GetInviteCodeResponseDto of(Parentchild parentchild) {
        return GetInviteCodeResponseDto.builder()
                .inviteCode(parentchild.getInviteCode())
                .build();
    }
}
