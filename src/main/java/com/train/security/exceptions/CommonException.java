package com.train.security.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonException extends RuntimeException{

    private String code;
    private HttpStatus status;

    public CommonException() {
        super();
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonException(String code,String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
