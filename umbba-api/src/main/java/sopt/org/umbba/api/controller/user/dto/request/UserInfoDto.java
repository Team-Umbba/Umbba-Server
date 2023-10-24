package sopt.org.umbba.api.controller.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import sopt.org.umbba.domain.domain.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfoDto {

    private Long userId;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "성별은 필수 입력 값입니다.")
    private String gender;

    @NotNull(message = "출생연도는 필수 입력 값입니다.")
    private Integer bornYear;

    private Boolean isMeChild;

    public static UserInfoDto of(User user) {
        return UserInfoDto.builder()
                .userId(user.getId())
                .name(user.getUsername())
                .gender(user.getGender())
                .bornYear(user.getBornYear())
                .isMeChild(user.isMeChild())
                .build();
    }
}
