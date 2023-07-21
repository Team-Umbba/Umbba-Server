package sopt.org.umbbaServer.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuestionSection {
    YOUNG(1L, "어린시절", 1),
    SCHOOL(2L, "학창시절", 1),
    GOLDEN(3L, "청춘시절", 2),
    COUPLE(4L, "연애시절", 1),
    MARRIAGE(5L, "우리가 만나고", 1),
    MARRIAGE2(5L, "우리가 만나고", 1); // 전연령 - 우리가 만나고
    ;

    private final Long sectionId; //findBySectionId 하면 안됨
    private final String value;
    private final int questionCount;
}