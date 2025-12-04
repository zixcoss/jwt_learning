package com.train.security.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseMessage {

    RESPONSE_SUCCESS("ระบบทำงานสำเร็จ", HttpStatus.OK),
    RESPONSE_ERROR("ระบบทำงานไม่สำเร็จ", HttpStatus.BAD_REQUEST),

    ERR_AUTH_001("อีเมลหรือรหัสผ่านไม่ถูกต้อง", HttpStatus.UNAUTHORIZED),
    ERR_AUTH_002("โทเค็นหมดอายุหรือไม่ถูกต้อง", HttpStatus.UNAUTHORIZED),
    ERR_AUTH_003("ไม่พบข้อมูลเข้าระบบ", HttpStatus.UNAUTHORIZED),

    ERR_USER_001("ไม่พบผู้ใช้งาน", HttpStatus.NOT_FOUND),
    ERR_USER_002("อีเมลนี้ถูกใช้งานแล้ว", HttpStatus.CONFLICT),

    SUC_USER_001("สมัครสมาชิกสำเร็จ", HttpStatus.CREATED),
    SUC_USER_002("ออกจากระบบสำเร็จ", HttpStatus.OK),
    ;

    private final String message;
    private final HttpStatus status;
}
