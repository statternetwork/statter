package com.synctech.statter.common.service.vo.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synctech.statter.base.entity.Miner;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MinerVo extends Miner {

    @ApiModelProperty(name = "td", value = "Mining tax completion time(the back end is formatted in the format of yyyy-MM-dd HH:mm:ss).")
    @JsonProperty("td")
    String taxDate;
    @ApiModelProperty(name = "taxAmount")
    @JsonProperty("taxAmount")
    String taxAmount;
    @ApiModelProperty(name = "ts", value = "The current mining tax process (including completion) is currently ongoing.31-Apply for mining tax (unpaid);32-Payment (unfinished process);33-Completed payment of mining taxes;")
    @JsonProperty("ts")
    int taxStage;

    @ApiModelProperty(name = "pd", value = "Mining pledge or redempt completion time(the back end is formatted in the format of yyyy-MM-dd HH:mm:ss).")
    @JsonProperty("pd")
    String pledgeDate;
    @ApiModelProperty(name = "pledgeAmount")
    @JsonProperty("pledgeAmount")
    String pledgeAmount;
    @ApiModelProperty(name = "ps", value = "The current mining pledge or redempt process (including completion) is currently ongoing.11-Apply for mining pledge (unpaid);12-Payment (unfinished process);13-Completed payment of mining pledge;21-Apply for mining redemption;23-Completed payment of mining redemption;")
    @JsonProperty("ps")
    int pledgerStage;
    @ApiModelProperty(name = "hash", value = "The average hash of the past minute")
    long hash;
    @ApiModelProperty(name = "online", value = "the miner is or not online")
    boolean online;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @ApiModelProperty(name = "canMining", value = "Whether the mining machine currently meets the front conditions of mining. Pre -mining conditions: complete the mining tax; complete the pledge of mining machines; the above two operations are completed before today;")
    boolean canMining;
//    @ApiModelProperty(name = "canTax", value = "Whether the mining machine can apply for a mining tax currently. You can pay the front conditions of the mine tax: This mining machine has never performed the mining tax operation;")
//    boolean canTax;
    @ApiModelProperty(name = "canPledger", value = "Whether the mining machine can currently apply for a mining machine pledge. The front conditions of the mining machine pledge: the mining machine pledge or redemption operation on this mining machine;")
    boolean canPledger;
    @ApiModelProperty(name = "canRedemption", value = "Whether the mining machine can currently apply for a mining machine. The front conditions that can be redeemed by the miner: the mining machine pledge operation completed on this mining machine has not yet applied for a mining machine pledge of redemption;")
    boolean canRedemption;

    public MinerVo(Miner miner) {
        super();
        super
                .setSn(miner.getSn())
                .setWalletAddress(miner.getWalletAddress())
                .setPromotionAddress(miner.getPromotionAddress())
                .setHasPledged(miner.isHasPledged()).setPledgeProcessId(miner.getPledgeProcessId())
                .setHasTaxed(miner.isHasTaxed()).setTaxProcessId(miner.getTaxProcessId())
                .setMaxHistoryHash(miner.getMaxHistoryHash())
                .setMachineId(miner.getMachineId())
        ;
    }

}
