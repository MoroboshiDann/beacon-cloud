package org.moroboshidan.common.exception;

import lombok.Getter;
import org.moroboshidan.common.enums.ExceptionEnums;

/**
 * 策略模块的异常对象
 * @author zjw
 * @description
 */
@Getter
public class StrategyException extends RuntimeException {
    private Integer code;

    public StrategyException(String message, Integer code) {
        super(message);
        this.code = code;
    }


    public StrategyException(ExceptionEnums enums) {
        super(enums.getMsg());
        this.code = enums.getCode();
    }
}
