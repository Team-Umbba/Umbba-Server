package sopt.org.umbbaServer.domain.user.social.apple;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sopt.org.umbbaServer.domain.user.social.apple.feign.AppleApiClient;
import sopt.org.umbbaServer.domain.user.social.apple.response.ApplePublicKeys;
import sopt.org.umbbaServer.domain.user.social.apple.verify.AppleClaimsValidator;
import sopt.org.umbbaServer.domain.user.social.apple.verify.AppleJwtParser;
import sopt.org.umbbaServer.domain.user.social.apple.verify.PublicKeyGenerator;
import sopt.org.umbbaServer.error.CustomException;
import sopt.org.umbbaServer.error.ErrorType;

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
