package com.statter.statter.base.entity;

import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

import static com.statter.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Promotion implements Serializable {

    Timestamp createdTime;

    Timestamp updatedTime;

    int status;
    int visible;

    /**
     * @see Wallet#address
     */
    @Schema(name = "address", description = "The income wallet address of the mining pool, defaults to the length " + WALLET_ADDRESS_LENGTH + ", the extensive string that has begun at the beginning of ST")
    String address;

    @Schema(name = "minerCount", description = "Number of mining machines under the jurisdiction")
    int minerCount;

    @Schema(description = "the statistics standard hash of the miners under the promotion")
    long hash;

    @Schema(name = "code", description = "the unique code of the promotion")
    String code;

    @Schema(name = "alias", description = "alias name")
    String alias;

    @Schema(name = "introduction", description = "The introduction about promotion")
    String introduction;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Schema(name = "manage secret key", description = "Used for asking or refreshing secret key")
    String managementKey;
    @Schema(name = "secret key", description = "Used for ask the api of the mining pool")
    String secretKey;
    @Schema(name = "secret key refresh time", description = "Secret key can only be refreshed once in 24 hours")
    Timestamp secretKeyUptTime;

//    @Schema(name = "authority", description = "Extend column for expanding modules of the mining pool in future, it's useless now.")
//    String authority;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
