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
public class ApplyForPromotion implements Serializable {

    Timestamp createdTime;

    Timestamp updatedTime;

    byte status;

    /**
     * @see Wallet#address
     */
    @ApiModelProperty(name = "address", value = "The income wallet address of the mining pool, defaults to the length " + WALLET_ADDRESS_LENGTH + ", the extensive string that has begun at the beginning of ST")
    String address;

    @ApiModelProperty(name = "alias", value = "alias name")
    String alias;

    @ApiModelProperty(name = "introduction", value = "The introduction about promotion")
    String introduction;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public enum Status {

        APPLY((byte) 1, "apply for promotion"),
        PASS((byte) 2, "the apply for promotion has been passed"),
        REFUSE((byte) 3, "the apply for promotion has been refused"),
        ;

        byte v;
        String description;

        Status(byte v, String description) {
            this.v = v;
            this.description = description;
        }

        public byte get() {
            return this.v;
        }

        public boolean compare(byte v) {
            return this.v == v;
        }

    }


}
