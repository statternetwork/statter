package com.statter.statter.mining.pool.api.controller.v1.miner;

import com.statter.statter.mining.pool.api.controller.v1.CommonController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "mining pool: miner")
@RequestMapping("statter/mining/pool/api/v1/miner")
@RestController()
public class MinerController extends CommonController {

}
