package com.statter.statter.constant.restful;

import com.statter.statter.constant.HttpStatusExtend;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerImpl {

    static long ClientAbortExceptionCount = 0, ClientAbortExceptionLastTime = 0;

    @ExceptionHandler(value = {Exception.class})
    public Object handleException(Exception e, HttpServletRequest request) {
        if (e instanceof AppBizException) {
            AppBizException appe = (AppBizException) e;
            log.warn(appe.getMessage());
            return DataResponse.fail(appe);
        } else if (e instanceof ClientAbortException) {
            handleClientAbortException();
            log.warn("Client break pipe: " + e.getMessage());
            return DataResponse.fail(e.getMessage());
        }
        log.error(e.getMessage(), e);
        System.exit(1);
        return DataResponse.fail(e);
    }

    void handleClientAbortException() {
        if (System.currentTimeMillis() - ClientAbortExceptionLastTime > 60000) ClientAbortExceptionCount = 0;
        if (ClientAbortExceptionCount > 100) System.exit(1);
        ClientAbortExceptionCount++;
        ClientAbortExceptionLastTime = System.currentTimeMillis();
    }

}
