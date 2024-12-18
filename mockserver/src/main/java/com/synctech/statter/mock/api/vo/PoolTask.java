package com.statter.statter.mock.api.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class PoolTask {

    int status;
    String blockHash;
    String workload;
    Block block;

}
