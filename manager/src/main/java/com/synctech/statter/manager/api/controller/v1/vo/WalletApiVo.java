package com.synctech.statter.manager.api.controller.v1.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public class WalletApiVo {

    @Data
    @NoArgsConstructor
    public static class RenameReq implements Serializable {

        @Schema(name = "a", description = "Wallet Address, defaults to the extension string that has begun at the beginning of ST.")
        @JsonProperty("a")
        String address;

        @Schema(name = "alias", description = "Alias name for this wallet account")
        @JsonProperty("alias")
        String alias;

    }

}
