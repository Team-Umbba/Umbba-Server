package sopt.org.umbbaServer.domain.user.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import sopt.org.umbbaServer.global.config.jwt.TokenDto;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserLoginResponseDto {
    private Long userId;

    private Boolean isNewUser;

    private String username;

    private String gender;

    private Integer bornYear;

    private TokenDto tokenDto;

    private SocialPlatform socialPlatform;

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

//    private String socialRefreshToken;

    public static UserLoginResponseDto of(boolean isRegistered, User loginUser, String accessToken) {
        TokenDto tokenDto = TokenDto.of(accessToken, loginUser.getRefreshToken());

        return UserLoginResponseDto.builder()
                .userId(loginUser.getId())
                .isNewUser(!isRegistered) // 만약 등록이 안되어있던 회원이면 isNewUser을 true로 설정
                .username(loginUser.getUsername())
                .gender(loginUser.getGender())
                .bornYear(loginUser.getBornYear())
                .tokenDto(tokenDto)
                .socialPlatform(loginUser.getSocialPlatform())
                .socialNickname(loginUser.getSocialNickname())
                .socialProfileImage(loginUser.getSocialProfileImage())
                .socialAccessToken(loginUser.getSocialAccessToken())
                /*, loginUser.getSocialRefreshToken()*/
                .build();
    }
}