package com.statter.statter.common.service.vo.info;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.statter.statter.base.entity.Miner;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Hash {

    @Schema(name = "h", description = "The average hash of the past minute")
    @JsonProperty("h")
    long h;

    @Schema(name = "t", description = "update time(timestamp)")
    @JsonProperty("t")
    long t;

    public boolean isOnline() {
        return t != 0 && (System.currentTimeMillis() - t) < Miner.TEN_MINUTE_MILLIS;
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
