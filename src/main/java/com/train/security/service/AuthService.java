package com.train.security.service;

import com.train.security.dto.request.LoginReq;
import com.train.security.dto.request.RegisterReq;
import com.train.security.dto.response.AuthRes;

public interface AuthService {
    AuthRes login(LoginReq req);
    void register(RegisterReq req);
    void logout();
    AuthRes refreshToken(String refreshToken);
}
