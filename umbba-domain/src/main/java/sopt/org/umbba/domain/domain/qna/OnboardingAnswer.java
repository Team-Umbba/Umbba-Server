package sopt.org.umbba.domain.domain.qna;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;

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
        throw new CustomException(ErroType.INVALID_ONBOARDING_ANSWER);
    }

}
