package com.synctech.statter.mining.pool.api.controller.v1.promotion.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import static com.synctech.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
public class PromotionSimpleVo implements Serializable {

    @ApiModelProperty(name = "code", value = "the unique code of the promotion")
    String code;

    @ApiModelProperty(name = "alias", value = "The alias name of the promotion")
    String alias;

    @ApiModelProperty(name = "introduction", value = "The introduction about promotion")
    String introduction;

}
