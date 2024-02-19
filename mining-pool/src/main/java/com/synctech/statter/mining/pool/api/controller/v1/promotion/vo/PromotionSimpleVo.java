package com.synctech.statter.mining.pool.api.controller.v1.promotion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

import static com.synctech.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
public class PromotionSimpleVo implements Serializable {

    @Schema(name = "code", description = "the unique code of the promotion")
    String code;

    @Schema(name = "alias", description = "The alias name of the promotion")
    String alias;

    @Schema(name = "introduction", description = "The introduction about promotion")
    String introduction;

}
