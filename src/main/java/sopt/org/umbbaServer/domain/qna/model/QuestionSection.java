package sopt.org.umbbaServer.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuestionSection {
    YOUNG("어린시절"),
    SCHOOL("학창시절"),
    GOLDEN("청춘시절");

    private final String value;
}