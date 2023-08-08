package sopt.org.umbba.domain.qna.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.umbba.global.exception.CustomException;
import sopt.org.umbba.global.exception.ErrorType;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum OnboardingAnswer {

    YES("응"),
    NO("아니"),
    SKIP("애매해");

    private final String value;

    public static OnboardingAnswer of(String value) {
        for (OnboardingAnswer answer : OnboardingAnswer.values()) {
            if (answer.getValue().equals(value)) {
                return answer;
            }
        }
        throw new CustomException(ErrorType.INVALID_ONBOARDING_ANSWER);
    }

}
