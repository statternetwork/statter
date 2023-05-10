package com.synctech.statter.mining.pool.api.controller.v1.promotion.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import static com.synctech.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

public class MinerSimpleVo implements Serializable {

    @ApiModelProperty(name = "sn", value = "Mining machine SN code, it's a string with a length of 32 by default.")
    @JsonProperty("sn")
    String sn;

    @ApiModelProperty(name = "wa", value = "The wallet address bundled by the mining machine, defaults to the extension string of length " + WALLET_ADDRESS_LENGTH + ", starting with ST.")
    @JsonProperty("wa")
    String walletAddress;

    @ApiModelProperty(name = "pa", value = "The ore pond wallet address corresponding to the wallet bundled by the mining machine, defaults to the extended string of length " + WALLET_ADDRESS_LENGTH + ", and ST starts.")
    @JsonProperty("pa")
    String promotionAddress;

    @ApiModelProperty(name = "hp", value = "Whether the miner pledge has been completed")
    @JsonProperty("hp")
    boolean hasPledged;

    @ApiModelProperty(name = "ht", value = "Whether the mine tax has been completed.")
    @JsonProperty("ht")
    boolean hasTaxed;

    @ApiModelProperty(name = "mhh", value = "The maximum hash of this mining machine.")
    @JsonProperty("mhh")
    long maxHistoryHash;

    @ApiModelProperty(name = "cpuModuleName", value = "The model of the mining machine's cpu")
    @JsonProperty("cpuModuleName")
    String cpuModuleName;

}
