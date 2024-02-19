package com.synctech.statter.mining.pool.api.controller.v1.promotion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import static com.synctech.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
@Accessors(chain = true)
public class PromotionInformationVo extends PromotionSimpleVo {

    @Schema(name = "address", description = "The income wallet address of the mining pool, defaults to the length "
            + WALLET_ADDRESS_LENGTH + ", the extensive string that has begun at the beginning of ST")
    String address;

    @Schema(description = "miner count belong to your promotion")
    int minerCount;

    @Schema(description = "the hash that all miners work theoretically")
    long theoreticalHash;

    @Schema(description = "the hash that miners work at present")
    long realTimeHash;

}
