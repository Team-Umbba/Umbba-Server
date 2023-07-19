package sopt.org.umbbaServer.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuestionType {
    TYPE1(1L, "타입 1"),
    TYPE2(2L, "타입 2"),
    TYPE3(3L, "타입 3"),
    TYPE4(4L, "타입 4"),
    TYPE5(5L, "타입 5"),
    MAIN(10L, "메인 타입"),
    FIX(11L, "고정된 질문"),
    YET(12L, "아직 사용하지 않는 질문");

    private final Long typeId;
    private final String description;
}
