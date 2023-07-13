package sopt.org.umbbaServer.global.common.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.slack.SlackApi;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@Component
@RequiredArgsConstructor
public class ControllerExceptionAdvice {

    private final SlackApi slackApi;

    /**
     * 400 BAD_REQUEST
     */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        return ApiResponse.error(ErrorType.REQUEST_VALIDATION_EXCEPTION, String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
    }

    /*@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnexpectedTypeException.class)
    protected ApiResponse handleUnexpectedTypeException(final UnexpectedTypeException e) {
        return ApiResponse.error(ErrorType.VALIDATION_WRONG_TYPE_EXCEPTION);
    }*/

    // Header에 원하는 Key가 없는 경우
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ApiResponse<Object> handlerMissingRequestHeaderException(final MissingRequestHeaderException e) {
        return ApiResponse.error(ErrorType.HEADER_REQUEST_MISSING_EXCEPTION);
    }

    /**
     * 500 INTERNEL_SERVER  // TODO 셔비스 단에서 예외가 꼼꼼하게 처리된 상태에서 500 에러를 가장 마지막에 던지도록 처리
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ApiResponse<Object> handleException(final Exception e, final HttpServletRequest request) throws IOException {
        //slackApi.sendAlert(e, request);  // TODO 슬랙 예외발생 알림 설정 시 사용

        log.error("Unexpected exception occurred: {}", e.getMessage(), e);

        return ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR);
    }

    /**
     * CUSTOM_ERROR
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse> handleCustomException(CustomException e) {

        log.error("CustomException occured: {}", e.getMessage(), e);

        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getErrorType(), e.getMessage()));
    }
}
