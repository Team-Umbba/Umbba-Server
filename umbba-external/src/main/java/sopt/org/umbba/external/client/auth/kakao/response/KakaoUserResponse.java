package sopt.org.umbba.external.client.auth.kakao.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserResponse {

    //받아올 땐 Long이지만, String으로 바꿔서 사용하기
    private Long id;

    private KakaoAccount kakaoAccount;
}