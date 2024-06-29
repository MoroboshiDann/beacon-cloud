package org.morosboshidan.enums;

import lombok.Getter;

/**
 * 响应信息中code和message的枚举
 */
@Getter
public enum SmsCodeEnum {
    PARAMETER_INVALIDATE(10001, "parameter invalidate"),;

    private Integer code;
    private String message;

    SmsCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
