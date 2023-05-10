package com.synctech.statter.manager.api.controller.v1.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.synctech.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PromotionInfoVo implements Serializable {

    @ApiModelProperty(name = "code", value = "the unique code of the promotion")
    String code;

    @ApiModelProperty(name = "alias", value = "The alias name of the promotion")
    String alias;

    @ApiModelProperty(name = "address", value = "The income wallet address of the mining pool is default. The default is " + WALLET_ADDRESS_LENGTH + " and the extensive string that has begun.")
    String address;

    @ApiModelProperty(name = "introduction", value = "The introduction about promotion")
    String introduction;

    @ApiModelProperty(name = "minerCount", value = "Number of mining machines under this mining pool.")
    int minerCount;

    @ApiModelProperty(name = "hash", value = "the real-time hash under this mining pool")
    long hash;

}
