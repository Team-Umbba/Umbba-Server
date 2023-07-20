package sopt.org.umbbaServer.global.common.advice;

import com.google.api.Http;
import feign.FeignException;
import io.jsonwebtoken.lang.UnknownClassException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.NestedServletException;
import sopt.org.umbbaServer.global.common.dto.ApiResponse;
import sopt.org.umbbaServer.global.exception.CustomException;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.util.slack.SlackApi;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
@Component
@RequiredArgsConstructor
public class ControllerExceptionAdvice {

    private final SlackApi slackApi;

    /**
     * 400 BAD_REQUEST
     */

    // FeignException은 @ControllerAdvice에서 처리하는 것이 권장되지 않음

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {

        Errors errors = e.getBindingResult();
        Map<String, String> validateDetails = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validateDetails.put(validKeyName, error.getDefaultMessage());
        }
        return ApiResponse.error(ErrorType.REQUEST_VALIDATION_EXCEPTION, validateDetails);
    }

    // 잘못된 타입으로 요청을 보낸 경우
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnexpectedTypeException.class)
    protected ApiResponse handleUnexpectedTypeException(final UnexpectedTypeException e) {
        return ApiResponse.error(ErrorType.VALIDATION_WRONG_TYPE_EXCEPTION);
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<Object> handlerMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return ApiResponse.error(ErrorType.VALIDATION_WRONG_TYPE_EXCEPTION, e.getMessage());
    }

    // Header에 원하는 Key가 없는 경우
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ApiResponse<Object> handlerMissingRequestHeaderException(final MissingRequestHeaderException e) {
        return ApiResponse.error(ErrorType.HEADER_REQUEST_MISSING_EXCEPTION);
    }

    // Enum 값에 존재하지 않는 request가 입력되었을 때
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ApiResponse<Object> handlerHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        return ApiResponse.error(ErrorType.VALIDATION_WRONG_HTTP_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ApiResponse<Object> handlerHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        return ApiResponse.error(ErrorType.INVALID_HTTP_METHOD);
    }




    /**
     * 500 INTERNEL_SERVER  // TODO 서비스 단에서 예외가 꼼꼼하게 처리된 상태에서 500 에러를 가장 마지막에 던지도록 처리
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ApiResponse<Object> handleException(final Exception e, final HttpServletRequest request) throws IOException {
        slackApi.sendAlert(e, request);

        log.error("Unexpected exception occurred: {}", e.getMessage(), e);

        return ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Object> handlerIllegalArgumentException(final IllegalArgumentException e) {
        return ApiResponse.error(ErrorType.INTERNAL_SERVER_ERROR, e.getMessage());
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IndexOutOfBoundsException.class)
    protected ApiResponse<Object> handlerIndexOutOfBoundsException(final IndexOutOfBoundsException e) {
        return ApiResponse.error(ErrorType.INDEX_OUT_OF_BOUNDS, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnknownClassException.class)
    protected ApiResponse<Object> handlerUnknownClassException(final UnknownClassException e) {
        return ApiResponse.error(ErrorType.JWT_SERIALIZE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoSuchElementException.class)
    protected ApiResponse<Object> handlerNoSuchElementException(final NoSuchElementException e) {
        return ApiResponse.error(ErrorType.OPTIONAL_EMPTY, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    protected ApiResponse<Object> handlerIncorrectResultSizeDataAccessException(final IncorrectResultSizeDataAccessException e) {
        return ApiResponse.error(ErrorType.NON_UNIQUE_RESULT_OF_QUERY, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NestedServletException.class)
    public ApiResponse<Object> handlerNestedServletException(final NestedServletException e) {
        return ApiResponse.error(ErrorType.INTERNAL_SERVLET_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ApiResponse<Object> handlerInvalidDataAccessApiUsageException(final InvalidDataAccessApiUsageException e) {
        return ApiResponse.error(ErrorType.NO_ENUM_TYPE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<Object> handlerDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return ApiResponse.error(ErrorType.DATA_INTEGRITY_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(JpaSystemException.class)
    public ApiResponse<Object> handlerJpaSystemException(final JpaSystemException e) {
        return ApiResponse.error(ErrorType.DATABASE_ERROR, e.getMessage());
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
