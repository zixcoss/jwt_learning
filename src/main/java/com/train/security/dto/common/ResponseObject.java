package com.train.security.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseObject {
    private boolean status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ErrorObject> errors;
}
