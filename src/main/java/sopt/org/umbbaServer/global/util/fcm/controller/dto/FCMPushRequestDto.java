package sopt.org.umbbaServer.global.util.fcm.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FCMPushRequestDto {

    private String targetToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String body;


    // Spring Scheduler를 이용해 Parentchild 테이블의 모든 값을 주기적으로 검사한 후 보낼 때 호출 -> 다수기기 or 주제구독 방식으로 다수의 사용자에 전송
    public static FCMPushRequestDto sendTodayQna(String section, String question) {

        PushMessage result = PushMessage.TODAY_QNA;
        result.setTitle(section);
        result.setBody(question);

        return FCMPushRequestDto.builder()
                .title(result.getTitle())
                .body(result.getBody())
                .build();
    }

    // QnAService or QnAController에서 특정 유저의 답변 입력 시 관계에 속한 상대 측 유저의 fcm 토큰으로 푸시 전송
    public static FCMPushRequestDto sendOpponentReply(String targetToken, String question) {

        PushMessage result = PushMessage.OPPONENT_REPLY;
        result.setBody(question);

        return FCMPushRequestDto.builder()
                .title(result.getTitle())
                .body(result.getBody())
                .build();
    }

}
