package com.synctech.statter.manager.api.controller.v1.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public class WalletApiVo {

    @Data
    @NoArgsConstructor
    public static class RenameReq implements Serializable {

        @ApiModelProperty(name = "a", value = "Wallet Address, defaults to the extension string that has begun at the beginning of ST.")
        @JsonProperty("a")
        String address;

        @ApiModelProperty(name = "alias", value = "Alias name for this wallet account")
        @JsonProperty("alias")
        String alias;

    }


}
