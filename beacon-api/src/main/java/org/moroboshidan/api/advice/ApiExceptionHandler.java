package org.moroboshidan.api.advice;

import org.moroboshidan.common.exception.ApiException;
import org.moroboshidan.api.util.R;
import org.moroboshidan.api.vo.ResultVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResultVO apiException(ApiException e) {
        return R.error(e);
    }
}
