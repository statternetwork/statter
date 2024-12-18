package com.statter.statter.common.pool.vo;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

import static com.statter.statter.constant.Constant.WALLET_ADDRESS_LENGTH;


@Data
public class MiningReportReq implements Serializable {

    @JsonProperty("sn")
    String sn;

    @JsonProperty("walletAddress")
    String walletAddress; // this column will be setting by the ledger server with the promotion address

    @JsonProperty("blockIndex")
    long blockIndex;

    @JsonProperty("createTime")
    String createTime;    // task created time ,from the gateway

    @JsonProperty("randomNumber")
    String randomNumber;  // compute result

    @JsonProperty("countTimes")
    long countTimes;   // the count time of the miner do compute operation while the sub task

    @JsonProperty("pid")
    String pid; // ledger id

//    @JsonProperty("machinesNum")
//    String machinesNum;

    public boolean validate() {
        if (StringUtils.isEmpty(this.sn) || this.sn.length() != 32) {
            return false;
        } else if (StringUtils.isEmpty(this.walletAddress) || this.walletAddress.length() != WALLET_ADDRESS_LENGTH) {
            return false;
        } else if (StringUtils.isEmpty(this.randomNumber) || !NumberUtil.isNumber(this.randomNumber)) {
            return false;
        } else if (blockIndex < 0) {
            return false;
        } else if (StringUtils.isEmpty(this.createTime)) {
            return false;
        }
        return true;
    }

    public boolean compare(PoolTask pt) {
        if (pt.getBlock().getBlockIndexValue() != this.blockIndex) {
            return false;
        } else if (!StringUtils.equals(pt.getBlock().getCreateTime(), this.getCreateTime())) {
            return false;
        }
        return true;
    }

}
