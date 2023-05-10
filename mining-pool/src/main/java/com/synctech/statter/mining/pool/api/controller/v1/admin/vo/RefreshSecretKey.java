package com.synctech.statter.mining.pool.api.controller.v1.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

public abstract class RefreshSecretKey {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class Req {

        @ApiModelProperty("pool address")
        String a;
        @ApiModelProperty("management key")
        String mk;

    }

}
