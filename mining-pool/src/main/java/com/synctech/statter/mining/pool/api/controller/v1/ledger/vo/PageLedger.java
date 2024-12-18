package com.statter.statter.mining.pool.api.controller.v1.ledger.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public abstract class PageLedger {

    @Data
    @Accessors(chain = true)
    public static class Resp {

        @Schema(name = "page")
        @JsonProperty("page")
        int page;

        @Schema(name = "size")
        @JsonProperty("size")
        int size;

        @Schema(name = "total", description = "total count belong the promotion")
        @JsonProperty("total")
        long total;

        @Schema(name = "data", description = "the ledger data list")
        @JsonProperty("data")
        List<LedgerVo> data = new ArrayList<>();

    }

}
