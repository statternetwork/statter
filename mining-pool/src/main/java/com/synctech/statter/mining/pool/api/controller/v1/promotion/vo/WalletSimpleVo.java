package com.synctech.statter.mining.pool.api.controller.v1.promotion.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

import static com.synctech.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
public class WalletSimpleVo implements Serializable {

    @Schema(name = "a", description = "Wallet Address, defaults to the extension string that has begun at the beginning of ST.")
    @JsonProperty("a")
    String address;

    @Schema(name = "pa", description = "The wallet address belongs to the ore pond wallet address, the default is "
            + WALLET_ADDRESS_LENGTH + ", the laid -up string that has begun.")
    @JsonProperty("pa")
    String promotionAddress;

    @Schema(name = "alias", description = "Alias name for this wallet account")
    @JsonProperty("alias")
    String alias;

    @Schema(name = "hp", description = "Whether the wallet pledge has been completed")
    @JsonProperty("hp")
    boolean hasPledged;

    @Schema(name = "minerCount", description = "Number of mining machines binding under this wallet address")
    int minerCount;

}
