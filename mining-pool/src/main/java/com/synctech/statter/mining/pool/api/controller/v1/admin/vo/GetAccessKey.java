package com.synctech.statter.mining.pool.api.controller.v1.admin.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

public abstract class GetAccessKey {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Req {

        @ApiModelProperty("pool address")
        String a;
        @ApiModelProperty("api access secret key")
        String sk;

        @Override
        public String toString() {
            return JSONObject.toJSONString(this);
        }

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Resp {

        @ApiModelProperty("access key")
        String ak;
        @ApiModelProperty("ak create time")
        String ct;
        @ApiModelProperty("ak create timestamp")
        Long ctl;
        @ApiModelProperty("ak will be expired at")
        String et;
        @ApiModelProperty("ak will be expired at this timestamp")
        Long etl;
//        @ApiModelProperty("the time zone of the server")
//        ZoneId zone = ZoneId.of("America/New_York");

        @Override
        public String toString() {
            return JSONObject.toJSONString(this);
        }

    }

}
