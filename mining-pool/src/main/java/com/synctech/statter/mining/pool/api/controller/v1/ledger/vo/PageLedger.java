package com.synctech.statter.mining.pool.api.controller.v1.ledger.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public abstract class PageLedger {

    @Data
    @Accessors(chain = true)
    public static class Resp {

        @ApiModelProperty(name = "page")
        @JsonProperty("page")
        int page;

        @ApiModelProperty(name = "size")
        @JsonProperty("size")
        int size;

        @ApiModelProperty(name = "total", value = "total count belong the promotion")
        @JsonProperty("total")
        long total;

        @ApiModelProperty(name = "data", value = "the ledger data list")
        @JsonProperty("data")
        List<LedgerVo> data = new ArrayList<>();

    }

}
