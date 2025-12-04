package com.train.security.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class LoginReq {
    private String email;
    private String password;
}
