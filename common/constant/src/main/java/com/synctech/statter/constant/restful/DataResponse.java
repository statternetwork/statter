package com.synctech.statter.constant.restful;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.synctech.statter.constant.HttpStatusExtend;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "Standard structure of the response data")
@Data
public class DataResponse<T extends Object> {

    @ApiModelProperty(name = "code", value = "Response state", notes = "Default 0 means normal, using data field data; when 0, it means abnormal, using the data of the MESSAGE field")
    int code = 0;
    @ApiModelProperty(name = "message", value = "Error message")
    String message = "";
    @ApiModelProperty(name = "data", value = "data body")
    T data = null;

    public DataResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> String success(T data) {
        return new DataResponse(0, "", data).toString();
    }

    public static String success() {
        return success(null);
    }

    public static String fail(int code, String msg) {
        return new DataResponse(code, msg, null).toString();
    }

    public static String fail() {
        return fail(HttpStatusExtend.ERROR_COMMON.value(), HttpStatusExtend.ERROR_COMMON.getReasonPhrase());
    }

    public static String fail(String msg) {
        return fail(HttpStatusExtend.ERROR_COMMON.value(), msg);
    }

    public static String fail(Exception e) {
        return fail(HttpStatusExtend.ERROR_COMMON.value(), e.getMessage());
    }

    public static String fail(HttpStatusExtend s) {
        return fail(s.value(), s.getReasonPhrase());
    }

    public static String fail(AppBizException e) {
        return fail(e.getCode(), e.getMessage());
    }

    @Override
    public String toString() {
        return JSON.parseObject(JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero)).toJSONString();
    }

}
