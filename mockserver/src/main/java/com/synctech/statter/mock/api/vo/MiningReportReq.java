package com.statter.statter.mock.api.vo;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

import static com.statter.statter.constant.Constant.WALLET_ADDRESS_LENGTH;
import static com.statter.statter.constant.Constant.checkWalletAddress;


@Data
public class MiningReportReq implements Serializable {

    @JsonProperty("sn")
    String sn;

    @JsonProperty("walletAddress")
    String walletAddress;

    @JsonProperty("blockIndex")
    long blockIndex;

    @JsonProperty("createTime")
    String createTime;

    @JsonProperty("randomNumber")
    String randomNumber;

    @JsonProperty("countTimes")
    long countTimes;

    @JsonProperty("pid")
    String pid;

    public boolean validate() {
        checkWalletAddress(this.walletAddress);
        if (StringUtils.isEmpty(this.sn) || this.sn.length() != 32) {
            return false;
//        } else if (StringUtils.isEmpty(this.walletAddress) || this.walletAddress.length() != WALLET_ADDRESS_LENGTH) {
//            return false;
        } else if (StringUtils.isEmpty(this.randomNumber) || !NumberUtil.isNumber(this.randomNumber)) {
            return false;
        } else if (blockIndex < 1) {
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
