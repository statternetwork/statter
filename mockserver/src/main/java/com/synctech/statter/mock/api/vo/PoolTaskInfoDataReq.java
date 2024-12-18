package com.statter.statter.mock.api.vo;

import lombok.Data;

@Data
public class PoolTaskInfoDataReq {

    String walletAddress;
    long blockIndex;
    String machinesNum;

}
