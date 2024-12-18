package com.statter.statter.manager.api.controller.v1.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.statter.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PromotionInfoVo implements Serializable {

    @Schema(name = "code", description = "the unique code of the promotion")
    String code;

    @Schema(name = "alias", description = "The alias name of the promotion")
    String alias;

    @Schema(name = "address", description = "The income wallet address of the mining pool is default. The default is "
            + WALLET_ADDRESS_LENGTH + " and the extensive string that has begun.")
    String address;

    @Schema(name = "introduction", description = "The introduction about promotion")
    String introduction;

    @Schema(name = "minerCount", description = "Number of mining machines under this mining pool.")
    int minerCount;

    @Schema(name = "hash", description = "the real-time hash under this mining pool")
    long hash;

}
