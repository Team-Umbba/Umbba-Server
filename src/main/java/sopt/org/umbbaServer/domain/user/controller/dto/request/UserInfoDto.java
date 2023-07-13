package sopt.org.umbbaServer.domain.user.controller.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.domain.user.model.User;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInfoDto {

    private Long userId;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "성별은 필수 입력 값입니다.")
    private String gender;

    @NotBlank(message = "출생연도는 필수 입력 값입니다.")
    private int bornYear;

    private boolean isMeChild;


    public static UserInfoDto of(User user) {
        return UserInfoDto.builder()
                .userId(user.getId())
                .name(user.getUsername())
                .gender(user.getGender())
                .bornYear(user.getBornYear())
                .build();
    }
}
