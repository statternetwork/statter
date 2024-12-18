package com.statter.statter.common.service.vo.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.statter.statter.base.entity.Wallet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WalletVo extends Wallet {

    @Schema(name = "pd", description = "Wallet pledge completion time(the back end is formatted in the format of yyyy-MM-dd HH:mm:ss).")
    @JsonProperty("pd")
    String pledgeDate;
    @Schema(name = "pledgeAmount")
    @JsonProperty("pledgeAmount")
    String pledgeAmount;
    @Schema(name = "ps", description = "The current wallet pledge process (including completion) is currently ongoing.21-Apply for redemption;23-Completed payment of redemption;")
    @JsonProperty("ps")
    int pledgerStage;

    @Schema(name = "canPledger", description = "Washing for wallets can currently apply for wallet pledge. The front conditions of the wallet pledge: the wallet pledge or redemption operation of the wallet is not performed on this address;")
    boolean canPledger;
    @Schema(name = "canRedemption", description = "Washing for a wallet can be redeemed for wallet pledge. The front conditions for the redemption of wallet pledge: the wallet pledge operation is completed on this address and the redemption of the mines is not yet applied for;")
    boolean canRedemption;

    public WalletVo() {
    }

}
