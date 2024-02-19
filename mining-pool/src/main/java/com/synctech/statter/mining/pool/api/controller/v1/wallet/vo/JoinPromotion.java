package com.synctech.statter.mining.pool.api.controller.v1.wallet.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import static com.synctech.statter.constant.Constant.WALLET_ADDRESS_LENGTH;

public abstract class JoinPromotion {

    @Data
    @Accessors(chain = true)
    public static class Req {

        @Schema(name = "a", description = "Wallet Address, defaults to the extension string that has begun at the beginning of ST.")
        @JsonProperty("a")
        String address;

        @Schema(name = "pa", description = "The wallet address belongs to the ore pond wallet address, the default is "
                + WALLET_ADDRESS_LENGTH + ", the laid -up string that has begun.")
        @JsonProperty("pa")
        String promotionAddress;

    }

    @Data
    @Accessors(chain = true)
    public static class Resp {

    }

}
