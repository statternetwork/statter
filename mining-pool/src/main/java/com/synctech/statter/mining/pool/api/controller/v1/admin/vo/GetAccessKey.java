package com.statter.statter.mining.pool.api.controller.v1.admin.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

        @Schema(name = "pool address")
        String a;
        @Schema(name = "api access secret key")
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

        @Schema(name = "access key")
        String ak;
        @Schema(name = "ak create time")
        String ct;
        @Schema(name = "ak create timestamp")
        Long ctl;
        @Schema(name = "ak will be expired at")
        String et;
        @Schema(name = "ak will be expired at this timestamp")
        Long etl;
        // @Schema(name = "the time zone of the server")
        // ZoneId zone = ZoneId.of("America/New_York");

        @Override
        public String toString() {
            return JSONObject.toJSONString(this);
        }

    }

}
