package sopt.org.umbba.external.client.auth.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import sopt.org.umbba.external.client.auth.apple.response.ApplePublicKeys;

@FeignClient(name = "apple-public-verify-client", url = "https://appleid.apple.com/auth")
public interface AppleApiClient {

    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();
}