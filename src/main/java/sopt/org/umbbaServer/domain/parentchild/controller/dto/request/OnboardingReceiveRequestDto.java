package sopt.org.umbbaServer.domain.parentchild.controller.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sopt.org.umbbaServer.domain.user.controller.dto.request.UserInfoDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OnboardingReceiveRequestDto {

    @NotBlank(message = "부모자식 관계 아이디는 필수 입력 값입니다.")
    private Long parentChildId;

    @NotNull
    private UserInfoDto userInfo;

    // TODO 선택질문에 대한 답변 필드 추가 필요
}
