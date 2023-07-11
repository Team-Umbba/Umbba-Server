package sopt.org.umbbaServer.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuestionSection {
    YOUNG(1L, "어린시절"),
    SCHOOL(2L, "학창시절"),
    GOLDEN(3L, "청춘시절"),
    COUPLE(4L, "연애시절"),
    MARRIAGE(5L, "결혼시절");

    private final Long sectionId;
    private final String value;
}