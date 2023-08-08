package sopt.org.umbba.external.client.auth.apple.verify;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppleClaimsValidator {

    private final String iss;
    private final String clientId;
//    private final String nonce; // iOS 멘토링에서 질문 후 사용 여부 결정

    public AppleClaimsValidator(
            @Value("${apple.iss}") String iss,
            @Value("${apple.client-id}") String clientId
//            @Value("${apple.nonce}") String nonce
    ) {
        this.iss = iss;
        this.clientId = clientId;
//        this.nonce = EncryptUtils.encrypt(nonce);
    }

    public boolean isValid(Claims claims) {
        return claims.getIssuer().contains(iss)
                && claims.getAudience().equals(clientId);
//                && claims.get(NONCE_KEY, String.class).equals(nonce);
    }
}

