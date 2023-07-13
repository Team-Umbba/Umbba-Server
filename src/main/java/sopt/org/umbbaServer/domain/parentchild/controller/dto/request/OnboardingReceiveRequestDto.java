package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OnboardingReceiveRequestDto {

    @NotBlank(message = "부모자식 관계 아이디는 필수 입력 값입니다.")
    private Long parentChildId;

    @NotNull
    @Valid
    private UserInfoDto userInfo;


    public void validate(Validator validator) {
        Set<ConstraintViolation<OnboardingReceiveRequestDto>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
