package sopt.org.umbba.global.util.fcm.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PushMessage {

    // 새로운 주제가 도착했을 떄
    TODAY_QNA("로부터 교신이 도착했어요",
            "'에 대한 질문에 답변하고 추억을 나눠보세요 ☺️(수신거부 : 설정 - 푸시알림 off)"),


    // 주제에 대한 상대의 답변이 입력되었을 때
    OPPONENT_REPLY("📞 상대방이 교신에 응답했어요",
            "'에 대한 상대의 답변을 확인해 볼까요? ☺️(수신거부 : 설정 - 푸시알림 off)");

    private String title;
    private String body;


}