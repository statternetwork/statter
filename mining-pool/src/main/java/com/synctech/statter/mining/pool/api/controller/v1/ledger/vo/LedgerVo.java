package com.synctech.statter.mining.pool.api.controller.v1.ledger.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class LedgerVo {

    long bi;
    String sn;
    String a;
    String p;
    Map<String, Object> data;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
