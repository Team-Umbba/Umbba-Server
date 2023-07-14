package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Set;

@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OnboardingInviteRequestDto {

    @NotNull
    @Valid
    private UserInfoDto userInfo;

    @JsonProperty("is_invitor_child")
    private Boolean isInvitorChild;

    private String relationInfo; // 아들 or 딸  |  아빠 or 엄마

    @JsonFormat(pattern = "kk:mm")
    private LocalTime pushTime;

    // TODO 선택질문에 대한 답변 필드 추가 필요

    public void validate(Validator validator) {
        Set<ConstraintViolation<OnboardingInviteRequestDto>> violations = validator.validate(this);
        log.info("DTO Validate - violations: {}", violations.toArray().toString());
        for (ConstraintViolation c : violations) {
            log.info("violations - {}", c.getMessage());
        }
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
