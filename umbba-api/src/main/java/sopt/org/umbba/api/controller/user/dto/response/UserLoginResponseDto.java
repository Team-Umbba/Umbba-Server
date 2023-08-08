package sopt.org.umbba.api.controller.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbba.api.config.jwt.TokenDto;
import sopt.org.umbba.domain.domain.user.SocialPlatform;
import sopt.org.umbba.domain.domain.user.User;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserLoginResponseDto {
    private Long userId;

    private Boolean isMatchFinish;

    private String username;

    private String gender;

    private Integer bornYear;

    private TokenDto tokenDto;

    private String fcmToken;

    private SocialPlatform socialPlatform;

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

//    private String socialRefreshToken;

    public static UserLoginResponseDto of(User loginUser, String accessToken) {
        TokenDto tokenDto = TokenDto.of(accessToken, loginUser.getRefreshToken());

        return UserLoginResponseDto.builder()
                .userId(loginUser.getId())
                .isMatchFinish(loginUser.isMatchFinish())
                .username(loginUser.getUsername())
                .gender(loginUser.getGender())
                .bornYear(loginUser.getBornYear())
                .tokenDto(tokenDto)
                .fcmToken(loginUser.getFcmToken())
                .socialPlatform(loginUser.getSocialPlatform())
                .socialNickname(loginUser.getSocialNickname())
                .socialProfileImage(loginUser.getSocialProfileImage())
                .socialAccessToken(loginUser.getSocialAccessToken())
                /*, loginUser.getSocialRefreshToken()*/
                .build();
    }
}