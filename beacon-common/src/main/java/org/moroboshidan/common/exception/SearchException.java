package org.moroboshidan.common.exception;

import org.moroboshidan.common.enums.ExceptionEnums;

public class SearchException extends RuntimeException {
    private Integer code;

    public SearchException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public SearchException(ExceptionEnums enums) {
        super(enums.getMsg());
        this.code = enums.getCode();
    }
}
