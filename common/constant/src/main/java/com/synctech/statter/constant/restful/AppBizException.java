package com.synctech.statter.constant.restful;

import com.synctech.statter.constant.HttpStatusExtend;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class AppBizException extends RuntimeException {

    int code;

    String message;

    public AppBizException() {
    }

    public AppBizException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public AppBizException(HttpStatusExtend httpStatusExtend) {
        this.code = httpStatusExtend.value();
        this.message = httpStatusExtend.getReasonPhrase();
    }

    public AppBizException(HttpStatusExtend httpStatusExtend, String msg) {
        this.code = httpStatusExtend.value();
        this.message = httpStatusExtend.getReasonPhrase() + ":" + msg;
    }

    public AppBizException(HttpStatusExtend httpStatusExtend, Exception e) {
        this.code = httpStatusExtend.value();
        if (null != e && StringUtils.isNotBlank(e.getMessage())) {
            this.message = httpStatusExtend.getReasonPhrase() + ":" + e.getMessage();
        }
    }

    public boolean compare(AppBizException e) {
        return this.code == e.getCode() && StringUtils.equals(this.message, e.getMessage());
    }

    public boolean compare(HttpStatusExtend httpStatusExtend) {
        return this.code == httpStatusExtend.value() && StringUtils.equals(this.message, httpStatusExtend.getReasonPhrase());
    }

}
