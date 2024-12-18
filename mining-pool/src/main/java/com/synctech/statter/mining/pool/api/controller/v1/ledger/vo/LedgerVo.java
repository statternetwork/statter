package com.statter.statter.mining.pool.api.controller.v1.ledger.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class LedgerVo {

    @Schema(name = "bi", description = "block index")
    long bi;
    @Schema(name = "sn", description = "machine serial code")
    String sn;
    @Schema(name = "a", description = "wallet address")
    String a;
    @Schema(name = "p", description = "promotion address")
    String p;
    @Schema(name = "data", description = "ledger data", example = "{'sn1':compute count1, 'sn2':compute count2, ...}")
    Map<String, Object> data;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
