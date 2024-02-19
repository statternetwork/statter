package com.synctech.statter.common.service.vo.info;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synctech.statter.base.entity.Miner;
import com.synctech.statter.base.entity.Process;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MinerVo extends Miner {

    @Schema(name = "td", description = "Mining tax completion time(the back end is formatted in the format of yyyy-MM-dd HH:mm:ss).")
    @JsonProperty("td")
    String taxDate;

    @Schema(name = "taxAmount")
    @JsonProperty("taxAmount")
    String taxAmount;

    @Schema(name = "ts", description = "The current mining tax process (including completion) is currently ongoing.31-Apply for mining tax (unpaid);32-Payment (unfinished process);33-Completed payment of mining taxes;")
    @JsonProperty("ts")
    int taxStage;

    @Schema(name = "pd", description = "Mining pledge or redempt completion time(the back end is formatted in the format of yyyy-MM-dd HH:mm:ss).")
    @JsonProperty("pd")
    String pledgeDate;

    @Schema(name = "pledgeAmount")
    @JsonProperty("pledgeAmount")
    String pledgeAmount;

    @Schema(name = "ps", description = "The current mining pledge or redempt process (including completion) is currently ongoing.11-Apply for mining pledge (unpaid);12-Payment (unfinished process);13-Completed payment of mining pledge;21-Apply for mining redemption;23-Completed payment of mining redemption;")
    @JsonProperty("ps")
    int pledgerStage;

    @Schema(name = "hash", description = "The average hash of the past minute")
    long hash;

    @Schema(name = "online", description = "the miner is or not online")
    boolean online;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Schema(name = "canMining", description = "Whether the mining machine currently meets the front conditions of mining. Pre -mining conditions: complete the mining tax; complete the pledge of mining machines; the above two operations are completed before today;")
    boolean canMining;

    @Schema(name = "canPledger", description = "Whether the mining machine can currently apply for a mining machine pledge. The front conditions of the mining machine pledge: the mining machine pledge or redemption operation on this mining machine;")
    boolean canPledger;

    @Schema(name = "canRedemption", description = "Whether the mining machine can currently apply for a mining machine. The front conditions that can be redeemed by the miner: the mining machine pledge operation completed on this mining machine has not yet applied for a mining machine pledge of redemption;")
    boolean canRedemption;

    public MinerVo(Miner miner) {
        super();
        super.setSn(miner.getSn()).setStatus(miner.getStatus()).setBindDate(miner.getBindDate()).setLeaveFactory(miner.getLeaveFactory()).setWalletAddress(miner.getWalletAddress()).setPromotionAddress(miner.getPromotionAddress()).setHasPledged(miner.isHasPledged()).setPledgeProcessId(miner.getPledgeProcessId()).setHasTaxed(miner.isHasTaxed()).setTaxProcessId(miner.getTaxProcessId()).setMaxHistoryHash(miner.getMaxHistoryHash()).setMachineId(miner.getMachineId()).setV(miner.getV()).setVer(miner.getVer());
    }

    public void processMiningImpl() {
        if (!this.isHasPledged() || StringUtils.isBlank(this.getPledgeDate()) || this.getPledgeDate().length() == 1) {
            this.setCanMining(false);
            return;
        }
        long nds = System.currentTimeMillis() / 86400000; // the days count away from 1970-01-01
        long pds = DateUtil.parse(this.getPledgeDate(), "yyyy-MM-dd HH:mm:ss").getTime() / 86400000;
        boolean miningPledgeWaitToNextDay = true;
        if (nds > pds) {
            this.setCanMining(true);
        } else if (!miningPledgeWaitToNextDay) {
            this.setCanMining(true);
        } else {
            this.setCanMining(false);
        }
    }

    public void processPledgeImpl(Process p) {
        this.setPledgerStage(p.getStage());
        this.setPledgeDate(DateUtil.format(p.getCreatedTime(), "yyyy-MM-dd HH:mm:ss"));
        this.setPledgeAmount(p.getAmount());
        if (Process.Stage.PledgeApply.compare(p.getStage())) {
            this.setCanPledger(false);
            this.setCanRedemption(false);
        } else if (Process.Stage.PledgeHasPayed.compare(p.getStage())) {
            this.setCanPledger(false);
            this.setCanRedemption(false);
        } else if (Process.Stage.PledgeComplete.compare(p.getStage())) {
            this.setCanPledger(false);
            this.setCanRedemption(true);
        } else if (Process.Stage.RedemptionApply.compare(p.getStage())) {
            this.setCanPledger(false);
            this.setCanRedemption(false);
        } else if (Process.Stage.RedemptionComplete.compare(p.getStage())) {
            this.setCanPledger(true);
            this.setCanRedemption(false);
        }
    }

}
