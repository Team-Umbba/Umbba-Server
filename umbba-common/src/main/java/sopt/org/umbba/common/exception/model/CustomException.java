package sopt.org.umbba.common.exception.model;

import lombok.Getter;
import sopt.org.umbba.common.exception.ErrorType;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorType errorType;

    public CustomException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public int getHttpStatus() {
        return errorType.getHttpStatusCode();
    }
}
