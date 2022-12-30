package com.synctech.statter.mining.pool.api.config.restful;

import cn.hutool.json.JSONObject;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerImpl {

    @ExceptionHandler(value = {Exception.class})
    public Object handleException(Exception e, HttpServletRequest req, HttpServletResponse resp) {
        if (e instanceof AppBizException) {
            AppBizException appEx = (AppBizException) e;
            log.warn(appEx.getMessage());
            resp.setStatus(HttpStatusExtend.OK.value());
            return new JSONObject().set("code", appEx.getCode()).set("message", appEx.getMessage()).toString();
        }
        log.error(e.getMessage(), e);
        resp.setStatus(HttpStatusExtend.INTERNAL_SERVER_ERROR.value());// unknown error
        return HttpStatusExtend.INTERNAL_SERVER_ERROR.getReasonPhrase();
    }

}
