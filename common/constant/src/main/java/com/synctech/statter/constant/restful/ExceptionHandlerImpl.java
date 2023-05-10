package com.synctech.statter.constant.restful;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerImpl {

    @ExceptionHandler(value = {Exception.class})
    public Object handleException(Exception e, HttpServletRequest request) {
        if (e instanceof AppBizException) {
            log.warn(e.getMessage());
            return DataResponse.fail((AppBizException) e);
        }
        log.error(e.getMessage(), e);
        return DataResponse.fail(e);
    }

}
