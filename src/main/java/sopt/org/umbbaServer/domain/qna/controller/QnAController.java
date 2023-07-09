package sopt.org.umbbaServer.domain.qna.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.umbbaServer.domain.qna.service.QnAService;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.config.jwt.JwtProvider;
import sopt.org.umbbaServer.global.exception.SuccessType;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class QnAController {

    private final QnAService qnAService;

    @GetMapping("/qna")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse getTodayQna(Principal principal) {

        return ApiResponse.success(SuccessType.GET_TODAY_QNA_SUCCESS, qnAService.getTodayQnA(JwtProvider.getUserFromPrincial(principal)));
    }

    @GetMapping("/dummy")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse dummy() {
        qnAService.createQnA();

        return ApiResponse.success(SuccessType.GET_TODAY_QNA_SUCCESS);
    }
}
