package com.statter.statter.mining.pool.api.controller.v1.promotion.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

import static com.statter.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
public class MinerSimpleVo implements Serializable {

    @Schema(name = "sn", description = "Mining machine SN code, it's a string with a length of 32 by default.")
    @JsonProperty("sn")
    String sn;

    @Schema(name = "wa", description = "The wallet address bundled by the mining machine, defaults to the extension string of length "
            + WALLET_ADDRESS_LENGTH + ", starting with ST.")
    @JsonProperty("wa")
    String walletAddress;

    @Schema(name = "pa", description = "The ore pond wallet address corresponding to the wallet bundled by the mining machine, defaults to the extended string of length "
            + WALLET_ADDRESS_LENGTH + ", and ST starts.")
    @JsonProperty("pa")
    String promotionAddress;

    @Schema(name = "hp", description = "Whether the miner pledge has been completed")
    @JsonProperty("hp")
    boolean hasPledged;

    @Schema(name = "ht", description = "Whether the mine tax has been completed.")
    @JsonProperty("ht")
    boolean hasTaxed;

    @Schema(name = "mhh", description = "The maximum hash of this mining machine.")
    @JsonProperty("mhh")
    long maxHistoryHash;

    @Schema(name = "cpuModuleName", description = "The model of the mining machine's cpu")
    @JsonProperty("cpuModuleName")
    String cpuModuleName;

}
