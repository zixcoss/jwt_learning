package com.train.security.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRes {
    private String token;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;
}
