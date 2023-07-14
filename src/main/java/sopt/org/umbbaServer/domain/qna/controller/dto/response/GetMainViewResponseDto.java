package sopt.org.umbbaServer.domain.qna.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.qna.model.QnA;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMainViewResponseDto {

    private int responseCase;  // case를 1,2,3으로 구분 (Client)

    private String section;
    private String topic;
    private Integer index;

    private String inviteCode;
    private String inviteUsername;
    private String installUrl;  // TODO Firebase Dynamic Link

    private Boolean relativeUserActive;


    // 1. 일반적인 메인 홈 정보
    public static GetMainViewResponseDto of (QnA qnA, int index) {
        return GetMainViewResponseDto.builder()
                .responseCase(1)
                .section(qnA.getQuestion().getSection().getValue())
                .topic(qnA.getQuestion().getTopic())
                .index(index)
                .build();
    }

    // 2. 아직 부모자식 관계가 매칭되지 않은 경우
    public static GetMainViewResponseDto of (String inviteCode, String inviteUsername, String installUrl) {
        return GetMainViewResponseDto.builder()
                .responseCase(2)
                .inviteCode(inviteCode)
                .inviteUsername(inviteUsername)
                .installUrl(installUrl)
                .build();
    }

    // 3. 부모자식 중 상대 측 유저가 탈퇴한 경우
    public static GetMainViewResponseDto of (boolean relativeUserActive) {
        return GetMainViewResponseDto.builder()
                .responseCase(3)
                .relativeUserActive(relativeUserActive)
                .build();
    }
}
