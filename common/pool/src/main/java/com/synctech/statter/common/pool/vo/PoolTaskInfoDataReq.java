package com.synctech.statter.common.pool.vo;

import lombok.Data;

@Data
public class PoolTaskInfoDataReq {

    String walletAddress;
    long blockIndex;
    String machinesNum;

}
