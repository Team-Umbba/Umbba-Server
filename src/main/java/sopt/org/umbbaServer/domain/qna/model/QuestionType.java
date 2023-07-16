package sopt.org.umbbaServer.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuestionType {

    TYPE1(1L, "타입1"),
    TYPE2(2L, "타입2"),
    TYPE3(3L, "타입3"),
    TYPE4(4L, "타입4"),
    TYPE5(5L, "타입5"),
    TYPE6(6L, "타입6"),
    TYPE7(7L, "타입7");

    private final Long typeId;
    private final String description;
}
