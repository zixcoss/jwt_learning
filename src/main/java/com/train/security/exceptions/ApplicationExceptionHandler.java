package com.train.security.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.train.security.constants.ResponseMessage;
import com.train.security.dto.common.ErrorObject;
import com.train.security.dto.common.ResponseObject;
import com.train.security.utils.ResponseHelper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApplicationExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ResponseObject resError = ResponseObject.builder()
                .status(false)
                .message(ResponseMessage.RESPONSE_ERROR.getMessage())
                .errors(new ArrayList<>(
                        Collections.singletonList(
                            ErrorObject.builder()
                                .code(ResponseMessage.ERR_AUTH_002.name())
                                .message(ResponseMessage.ERR_AUTH_002.getMessage())
                                .build()
                )))
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(resError));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> loginExceptionHandler(BadCredentialsException ex) {
        log.warn("Handling BadCredentialsException: {}", ex.getMessage());
        ResponseObject resError = ResponseObject.builder()
                .status(false)
                .message(ResponseMessage.RESPONSE_ERROR.getMessage())
                .errors(new ArrayList<>(
                        Collections.singletonList(
                            ErrorObject.builder()
                                .code(ResponseMessage.ERR_AUTH_001.name())
                                .message(ResponseMessage.ERR_AUTH_001.getMessage())
                                .build()
                )))
                .build();
        return ResponseHelper.response(ResponseMessage.ERR_AUTH_001.getStatus(), resError);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<Object> commonExceptionHandler(CommonException ex) {
        log.warn("Common Exception code : {}" ,ex.getCode());
        ResponseObject resError = ResponseObject.builder()
                .status(false)
                .message(ResponseMessage.RESPONSE_ERROR.getMessage())
                .errors(new ArrayList<>(
                        Collections.singletonList(
                            ErrorObject.builder()
                                .code(ex.getCode())
                                .message(ex.getMessage())
                                .build()
                )))
                .build();
        return ResponseHelper.response(ex.getStatus(), resError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception ex) {
        log.warn("Handling generic exception: {}", ex.getMessage(), ex);
        ResponseObject resError = ResponseObject.builder()
                .status(false)
                .message(ResponseMessage.RESPONSE_ERROR.getMessage())
                .build();
        return ResponseHelper.response(HttpStatus.INTERNAL_SERVER_ERROR, resError);
    }
}
