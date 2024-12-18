package com.statter.statter.ledger.api.controller.v1.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

public abstract class WorkloadDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Req {

        @Schema(name = "miner sn")
        String sn;
        @Schema(name = "question block index")
        long bi;
        @Schema(name = "miner workload")
        long c;

        @Override
        public String toString() {
            return JSONObject.toJSONString(this);
        }

        public boolean validate() {
            return StringUtils.hasText(sn) && bi > 0 && c > 0;
        }

    }

}
