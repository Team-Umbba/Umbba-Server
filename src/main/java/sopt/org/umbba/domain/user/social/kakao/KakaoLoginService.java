package sopt.org.umbba.external.client.auth.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.umbba.domain.user.model.User;
import sopt.org.umbba.domain.user.social.kakao.feign.KakaoApiClient;
import sopt.org.umbba.domain.user.social.kakao.feign.KakaoAuthApiClient;
import sopt.org.umbba.domain.user.social.kakao.response.KakaoAccessTokenResponse;
import sopt.org.umbba.domain.user.social.kakao.response.KakaoUserResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class KakaoLoginService {

    @Value("${kakao.client-id}")
    private String CLIENT_ID;
    @Value("${kakao.authorization-grant-type}")
    private String GRANT_TYPE;
    @Value("${kakao.redirect-uri}")
    private String REDIRECT_URL;

    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final KakaoApiClient kakaoApiClient;

    public String getKakaoAccessToken(String code) {
        // Authorization code로 Access Token 불러오기
        KakaoAccessTokenResponse tokenResponse = kakaoAuthApiClient.getOAuth2AccessToken(
                GRANT_TYPE,
                CLIENT_ID,
                REDIRECT_URL,
                code
        );
        return tokenResponse.getAccessToken();
        // Refresh 토큰은 필요 없는거 맞나?
        // 1. 만약 필요하다면 클라한테서 함께 받아온다음
        // 2. login 메서드 -> setKakaoInfo 메서드 호출할 때 같이 받아오는 작업 필요
    }

    public String getKakaoId(String socialAccessToken) {

        // Access Token으로 유저 정보 불러오기
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + socialAccessToken);

        String kakaoId = Long.toString(userResponse.getId()); //Social ID를 조회

        return kakaoId;
    }

    public void setKakaoInfo(User loginUser, String socialAccessToken) {

        // Access Token으로 유저 정보 불러오기
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + socialAccessToken);

        loginUser.updateSocialInfo(userResponse.getKakaoAccount().getProfile().getNickname(),
                userResponse.getKakaoAccount().getProfile().getProfileImageUrl(),
                socialAccessToken); //Kakao의 Access 토큰도 매번 업데이트
    }

}