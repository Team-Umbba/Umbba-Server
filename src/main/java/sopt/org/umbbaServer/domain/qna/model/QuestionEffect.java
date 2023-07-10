package sopt.org.umbbaServer.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum QuestionEffect {
    DREAM("꿈에 대한 생각"),
    SYMPATHY("공감대형성"),
    HOOKING("후킹 요소");

    private final String value;
}
