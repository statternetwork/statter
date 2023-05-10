package com.synctech.statter.base.entity;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

import static com.synctech.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Promotion implements Serializable {

    Timestamp createdTime;

    Timestamp updatedTime;

    byte status;

    /**
     * @see Wallet#address
     */
    @ApiModelProperty(name = "address", value = "The income wallet address of the mining pool, defaults to the length " + WALLET_ADDRESS_LENGTH + ", the extensive string that has begun at the beginning of ST")
    String address;

    @ApiModelProperty(name = "minerCount", value = "Number of mining machines under the jurisdiction")
    int minerCount;

    @ApiModelProperty(value = "the statistics standard hash of the miners under the promotion")
    long hash;

    @ApiModelProperty(name = "code", value = "the unique code of the promotion")
    String code;

    @ApiModelProperty(name = "alias", value = "alias name")
    String alias;

    @ApiModelProperty(name = "introduction", value = "The introduction about promotion")
    String introduction;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ApiModelProperty(name = "manage secret key", value = "Used for asking or refreshing secret key")
    String managementKey;
    @ApiModelProperty(name = "secret key", value = "Used for ask the api of the mining pool")
    String secretKey;
    @ApiModelProperty(name = "secret key refresh time", value = "Secret key can only be refreshed once in 24 hours")
    Timestamp secretKeyUptTime;

//    @ApiModelProperty(name = "authority", value = "Extend column for expanding modules of the mining pool in future, it's useless now.")
//    String authority;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
