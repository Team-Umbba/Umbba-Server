package sopt.org.umbba.api.service.user.social.apple;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.org.umbba.api.service.user.social.apple.verify.AppleClaimsValidator;
import sopt.org.umbba.api.service.user.social.apple.verify.AppleJwtParser;
import sopt.org.umbba.api.service.user.social.apple.verify.PublicKeyGenerator;
import sopt.org.umbba.common.exception.ErrorType;
import sopt.org.umbba.common.exception.model.CustomException;
import sopt.org.umbba.external.client.auth.apple.AppleApiClient;
import sopt.org.umbba.external.client.auth.apple.response.ApplePublicKeys;


import java.security.PublicKey;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AppleLoginService {

    private final AppleApiClient appleApiClient;
    private final AppleJwtParser appleJwtParser;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public String getAppleId(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleApiClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        return claims.getSubject();
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new CustomException(ErrorType.INVALID_APPLE_CLAIMS);
        }
    }

}
