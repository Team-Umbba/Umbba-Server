package sopt.org.umbba.common.sqs.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PushMessage {

    // 새로운 주제가 도착했을 때
    TODAY_QNA("로부터 교신이 도착했어요",
            "'에 대한 질문에 답변하고 추억을 나눠보세요 ☺️(수신거부 : 설정 - 푸시알림 off)"),


    // 주제에 대한 상대의 답변이 입력되었을 때
    OPPONENT_REPLY("📞 상대방이 교신에 응답했어요",
            "'에 대한 상대의 답변을 확인해 볼까요? ☺️(수신거부 : 설정 - 푸시알림 off)"),


    // 아직 상대방이 답변하지 않았을 때 (리마인드용)
    OPPONENT_REMIND_24("📞 질문이 당신을 기다리고 있어요",
            "'에 대한 질문에 답변하고 추억을 나눠보세요 ☺️(수신거부 : 설정 - 푸시알림 off)"),

    OPPONENT_REMIND_72("📞 질문이 당신을 계속 기다리고 있어요",
            "'에 대한 질문에 답변하고 추억을 나눠보세요 ☺️(수신거부 : 설정 - 푸시알림 off)");

    private String title;
    private String body;


}
