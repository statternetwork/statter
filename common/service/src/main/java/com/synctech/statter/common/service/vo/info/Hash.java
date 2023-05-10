package com.synctech.statter.common.service.vo.info;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synctech.statter.base.entity.Miner;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Hash {

    @ApiModelProperty(name = "h", value = "The average hash of the past minute")
    @JsonProperty("h")
    long h;

    @ApiModelProperty(name = "t", value = "update time(timestamp)")
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
