package sopt.org.umbbaServer.util.slack;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.umbbaServer.error.ApiResponse;

@RestController
@RequestMapping("/test")
public class SlackTestController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse test() {
        throw new IllegalArgumentException();
    }
}