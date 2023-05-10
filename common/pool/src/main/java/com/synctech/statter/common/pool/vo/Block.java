package com.synctech.statter.common.pool.vo;

import lombok.Data;

@Data
public class Block {

    String blockIndex;
    String headHash;
    String endHash;
    String randomNumber; // the computing result of the miner
    String path;     // block file relative path
    String createTime;
    int onMingChain;
    int counter;
    int curCounter;
    int cCount;
    String pid; // ledger id

    String mingProfit;

    public long getBlockIndexValue() {
        return Long.parseLong(this.blockIndex);
    }

}
