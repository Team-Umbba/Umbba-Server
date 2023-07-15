package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetInvitationResponseDto {

    private int responseCase;  // case를 1,2,3으로 구분 (Client)

    // 예외상황에 따른 필드
    private String inviteCode;
    private String inviteUsername;
    private String installUrl;  // TODO Firebase Dynamic Link

    private Boolean relativeUserActive;


    // 1. 오늘의 질문을 조회한 일반적인 경우
    public static GetInvitationResponseDto of () {
        return GetInvitationResponseDto.builder()
                .responseCase(1)
                .relativeUserActive(true)
                .build();
    }

    // 2. 아직 부모자식 관계가 매칭되지 않은 경우
    public static GetInvitationResponseDto of (String inviteCode, String inviteUsername, String installUrl) {
        return GetInvitationResponseDto.builder()
                .responseCase(2)
                .inviteCode(inviteCode)
                .inviteUsername(inviteUsername)
                .installUrl(installUrl)
                .relativeUserActive(true)
                .build();
    }

    // 3. 부모자식 중 상대 측 유저가 탈퇴한 경우
    public static GetInvitationResponseDto of (boolean relativeUserActive) {
        return GetInvitationResponseDto.builder()
                .responseCase(3)
                .relativeUserActive(relativeUserActive)
                .build();
    }
}
