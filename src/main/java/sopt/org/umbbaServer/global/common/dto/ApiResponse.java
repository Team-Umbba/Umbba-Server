package sopt.org.umbbaServer.global.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sopt.org.umbbaServer.global.exception.ErrorType;
import sopt.org.umbbaServer.global.exception.SuccessType;

@Getter
@JsonPropertyOrder({"code", "message", "data"})
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private final int status;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static ApiResponse success(SuccessType successType) {
        return new ApiResponse<>(successType.getHttpStatusCode(), successType.getMessage());
    }

    public static <T> ApiResponse<T> success(SuccessType successType, T data) {
        return new ApiResponse<T>(successType.getHttpStatusCode(), successType.getMessage(), data);
    }

    public static ApiResponse error(ErrorType errorType) {
        return new ApiResponse<>(errorType.getHttpStatusCode(), errorType.getMessage());
    }

    public static ApiResponse error(ErrorType errorType, String message) {
        return new ApiResponse<>(errorType.getHttpStatusCode(), message);
    }
}
