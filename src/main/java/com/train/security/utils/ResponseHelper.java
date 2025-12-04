package com.train.security.utils;

import com.train.security.dto.common.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ResponseHelper {
    ResponseHelper(){throw new IllegalArgumentException();}

    private final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static ResponseEntity<Object> response(HttpStatus status, Object body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if(RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            httpHeaders.add("Request-Date", request.getHeader("Request-Date"));
        }
        httpHeaders.add("Response-Date", LocalDateTime.now().format(dateTimeFormat));
        return ResponseEntity.status(status).headers(httpHeaders).body(body);
    }

    public static ResponseEntity<Object> success(String message){
        return response(HttpStatus.OK,
                ResponseObject.builder()
                        .status(true)
                        .message(message)
                        .build()
                );
    }

    public static ResponseEntity<Object> successWithData(String message, Object obj){
        Map<String, Object> data = new HashMap<>();
        data.put("status", true);
        data.put("message", message);
        data.put("data", obj);
        return response(HttpStatus.OK,data);
    }
}
