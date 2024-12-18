package com.statter.statter.mock.api.vo;

import lombok.Data;

@Data
public class Block {

    String blockIndex;
    String headHash;
    String endHash;
    String randomNumber;
    String path;
    String createTime;
    int onMingChain;
    int counter;
    int curCounter;

    public long getBlockIndexValue() {
        return Long.parseLong(this.blockIndex);
    }

}
