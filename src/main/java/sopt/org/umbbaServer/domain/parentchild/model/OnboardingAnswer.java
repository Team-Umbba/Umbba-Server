package sopt.org.umbbaServer.domain.parentchild.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OnboardingAnswer {

    YES("응"),
    NO("아니"),
    DONT_KNOW("잘 모르겠어");

    private final String value;
}
