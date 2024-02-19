package com.synctech.statter.mining.pool.api.controller.v1.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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

        @Schema(name = "pool address")
        String a;
        @Schema(name = "management key")
        String mk;

    }

}
