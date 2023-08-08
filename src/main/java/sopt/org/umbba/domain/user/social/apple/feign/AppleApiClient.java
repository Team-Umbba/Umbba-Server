package sopt.org.umbba.domain.user.social.apple.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import sopt.org.umbba.domain.user.social.apple.response.ApplePublicKeys;

@FeignClient(name = "apple-public-verify-client", url = "https://appleid.apple.com/auth")
public interface AppleApiClient {

    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();
}