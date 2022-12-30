package com.synctech.statter.common.service.vo.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synctech.statter.base.entity.Wallet;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WalletVo extends Wallet {

    @ApiModelProperty(name = "pd", value = "Wallet pledge completion time(the back end is formatted in the format of yyyy-MM-dd HH:mm:ss).")
    @JsonProperty("pd")
    String pledgeDate;
    @ApiModelProperty(name = "pledgeAmount")
    @JsonProperty("pledgeAmount")
    String pledgeAmount;
    @ApiModelProperty(name = "ps", value = "The current wallet pledge process (including completion) is currently ongoing.21-Apply for redemption;23-Completed payment of redemption;")
    @JsonProperty("ps")
    int pledgerStage;

    @ApiModelProperty(name = "canPledger", value = "Washing for wallets can currently apply for wallet pledge. The front conditions of the wallet pledge: the wallet pledge or redemption operation of the wallet is not performed on this address;")
    boolean canPledger;
    @ApiModelProperty(name = "canRedemption", value = "Washing for a wallet can be redeemed for wallet pledge. The front conditions for the redemption of wallet pledge: the wallet pledge operation is completed on this address and the redemption of the mines is not yet applied for;")
    boolean canRedemption;

    public WalletVo() {
    }

}
