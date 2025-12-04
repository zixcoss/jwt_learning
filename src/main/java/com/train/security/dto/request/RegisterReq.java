package com.train.security.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class RegisterReq {
    private String email;
    private String password;
    private String confirmPassword;
}
