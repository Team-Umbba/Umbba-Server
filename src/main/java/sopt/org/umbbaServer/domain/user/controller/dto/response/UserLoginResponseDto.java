package sopt.org.umbbaServer.domain.user.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.umbbaServer.global.config.jwt.TokenDto;
import sopt.org.umbbaServer.domain.user.model.User;
import sopt.org.umbbaServer.domain.user.social.SocialPlatform;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {
    private Long userId;

    private String username;

    private String gender;

    private Integer bornYear;

    private TokenDto tokenDto;

    private SocialPlatform socialPlatform;

    private String socialNickname;

    private String socialProfileImage;

    private String socialAccessToken;

//    private String socialRefreshToken;

    public static UserLoginResponseDto of(User loginUser, String accessToken) {
        TokenDto tokenDto = TokenDto.of(accessToken, loginUser.getRefreshToken());

        return new UserLoginResponseDto(
                loginUser.getId(), loginUser.getUsername(), loginUser.getGender(), loginUser.getBornYear(),
                tokenDto,
                loginUser.getSocialPlatform(), loginUser.getSocialNickname(), loginUser.getSocialProfileImage(),
                loginUser.getSocialAccessToken()/*, loginUser.getSocialRefreshToken()*/);
    }
}