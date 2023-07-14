package sopt.org.umbbaServer.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OnboardingAnswer {

    YES("응"),
    NO("아니"),
    SKIP("잘 모르겠어");

    private final String value;
}
