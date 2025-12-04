package com.train.security.controllers;

import com.train.security.constants.ResponseMessage;
import com.train.security.dto.common.UserDetailImpl;
import com.train.security.dto.request.LoginReq;
import com.train.security.dto.request.RefreshTokenReq;
import com.train.security.dto.request.RegisterReq;
import com.train.security.dto.response.AuthRes;
import com.train.security.exceptions.CommonException;
import com.train.security.service.AuthService;
import com.train.security.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/ping")
    public ResponseEntity<Object> ping() {
        return ResponseHelper.success("ping from auth controller success");
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginReq req) {
        return ResponseHelper.successWithData(
                ResponseMessage.RESPONSE_SUCCESS.getMessage(),
                authService.login(req)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout() {
        authService.logout();
        return ResponseHelper.success(ResponseMessage.SUC_USER_002.getMessage());
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterReq req) {
        authService.register(req);
        return ResponseHelper.success(ResponseMessage.SUC_USER_001.getMessage());
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshTokenReq req) {
        return ResponseHelper.successWithData(ResponseMessage.RESPONSE_SUCCESS.getMessage(),
                authService.refreshToken(req.getRefreshToken()));
    }
}
