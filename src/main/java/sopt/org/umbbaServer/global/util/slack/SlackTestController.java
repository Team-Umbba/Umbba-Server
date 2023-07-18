package sopt.org.umbbaServer.global.util.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/test")
public class SlackTestController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse test() {
        log.info("[test] API 실행");
        throw new IllegalArgumentException();
    }
}