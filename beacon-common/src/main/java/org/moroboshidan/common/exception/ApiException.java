package org.moroboshidan.common.exception;

import lombok.Getter;
import org.moroboshidan.common.enums.ExceptionEnums;

@Getter
public class ApiException extends RuntimeException {
    private Integer code;

    public ApiException(String message, Integer code) {
        super(message);
        this.code = code;
    }


    public ApiException(ExceptionEnums enums) {
        super(enums.getMsg());
        this.code = enums.getCode();
    }
}
