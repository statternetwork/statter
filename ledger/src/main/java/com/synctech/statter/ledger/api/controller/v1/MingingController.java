package com.synctech.statter.ledger.api.controller.v1;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.common.pool.service.PoolService;
import com.synctech.statter.common.pool.vo.MiningReportReq;
import com.synctech.statter.common.pool.vo.PoolTask;
import com.synctech.statter.common.service.service.MinerService;
import com.synctech.statter.common.service.service.PromotionService;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.ledger.api.service.LedgerService;
import com.synctech.statter.ledger.api.service.QuestionService;
import com.synctech.statter.util.JSONUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(value = "api about mining")
@RequestMapping("v1/mining")
@RestController
public class MingingController {

    private static final String MiningReportExample = "{\"walletAddress\":\"11111111\",\"blockIndex\":0,\"createTime\":\"11111111\",\"randomNumber\":\"11111111\",\"countTimes\": 11111111,\"sn\":\"111\"}";

    @Resource
    MinerService minerService;

    @Resource
    PromotionService promotionService;

    @Resource
    QuestionService questionService;
    @Resource
    LedgerService ledgerService;

    @Resource
    PoolService poolService;

    public static void main(String[] args) {
        StopWatch w = new StopWatch();
        JSONObject json = new JSONObject();
        w.start("add");
        for (int i = 0; i < 10000; i++) json.put("_" + i, RandomUtil.randomNumbers(6));
        w.stop();
        w.start("sum");
        long v = json.entrySet().stream().map(entry -> Long.valueOf((String) entry.getValue())).reduce(0L, Long::sum);
        //long v = m.entrySet().stream().map(ent -> Long.valueOf(ent.getValue())).reduce(0L, Long::sum);
        w.stop();
        System.out.println(w.prettyPrint());
        System.out.println("================================== " + v + " ==================================");
    }

    @ApiOperation(httpMethod = "GET", value = "query block index")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GetMapping("/index")
    public String index() {
        long bi = questionService.getBlockIndex();
        return DataResponse.success(bi);
    }

    @ApiOperation(httpMethod = "GET", value = "query mining task")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GetMapping("/task/{sn}")
    public String task(@ApiParam(value = "miner sn", type = "String", required = true) @PathVariable("sn") String sn) {
//        System.out.println("api task catch: find miner info");
        MinerVo m = minerService.findBySn(sn);
        if (null == m) {
            log.warn(HttpStatusExtend.ERROR_MINER_NOT_FOUND.getReasonPhrase());
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        } else if (!m.isCanMining()) {// the miner with this sn cannot do mining
            throw new AppBizException(HttpStatusExtend.ERROR_MINING_MINER_CANNOT_MINING);
        }
        // To mine the mining machine, you need to receive the task first. Here you can verify the legitimacy of the equipment to avoid the useless work that can be submitted.
//        System.out.println("api task catch: query block index");
        long bi = questionService.getBlockIndex();
//        System.out.println("api task catch: find promotion info");
        Promotion p = promotionService.get(m.getPromotionAddress());
//        System.out.println("api task catch: query pool task");
        PoolTask pt = questionService.getPoolTask(bi, m.getPromotionAddress(), p.getHash() + "");// The registration address is the promotion address. The wallet on the mining machine will be recorded once during the correct submission to increase the expansion
//        System.out.println("api task catch: analyze workload");
        String workload = questionService.analyzeWorkload(pt.getWorkload());
        pt.setWorkload(workload);
//        System.out.println("api task catch: end");
        return DataResponse.success(pt);
    }

    @ApiOperation(httpMethod = "POST", value = "report mining result")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @PostMapping("/report")
    public String report(@ApiParam(value = "mining result content", type = "json", required = true, example = MingingController.MiningReportExample)
                         @RequestBody @Validated MiningReportReq req) {
        if (!req.validate()) {
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        MinerVo m = minerService.findBySn(req.getSn());
        if (null == m) {
            log.warn(HttpStatusExtend.ERROR_MINER_NOT_FOUND.getReasonPhrase());
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        } else if (!m.isCanMining()) {
            log.warn(HttpStatusExtend.ERROR_MINING_MINER_CANNOT_MINING.getReasonPhrase());
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }

        long bi = questionService.getBlockIndex();
        if (bi != req.getBlockIndex()) {
            throw new AppBizException(HttpStatusExtend.ERROR_MINING_REPORT_WRONG_BLOCK_INDEX);
        }
        Promotion p = promotionService.get(m.getPromotionAddress());
        PoolTask pt = questionService.getPoolTask(bi, m.getPromotionAddress(), p.getHash() + "");
        log.debug("report request pool task = {}", JSONUtils.toJson(pt));
        if (!req.compare(pt)) {
            throw new AppBizException(HttpStatusExtend.ERROR_MINING_REPORT_WRONG_QUESTION);
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // The task is legal, and the computing process is legal,
        // so the calculation of the mining machine,
        // whether the result is correct or not,
        // needs to be recorded.
        long c = ledgerService.count(bi, m.getSn(), req.getCountTimes());
        if (!pt.validate(req.getRandomNumber())) {// Verify whether the calculation results are correct
            throw new AppBizException(HttpStatusExtend.ERROR_MINING_REPORT_WRONG_COMPUTE_RESULT);
        }

        // Extracting the current block height of the ledger is equivalent to taking out a copy of the ledger and generating a copy of the ledger
        LedgerService.LedgerExractDto dto = ledgerService.extract(bi, m.getSn(), m.getWalletAddress(), m.getPromotionAddress());
        req.setPid(dto.getId());
//        long tc = dto.getDataMap().entrySet().stream().map(ent -> Long.valueOf(ent.getValue())).reduce(0L, Long::sum);
//        req.setMachinesNum(tc + "");
        req.setWalletAddress(m.getPromotionAddress());// In the method of mining pool, report the mining results to the gateway
        try {
            poolService.commitPoolTaskUrl(req);
        } catch (AppBizException e) {
            log.warn("Mining service-mining results submitted: mining service verification passed, but errors occurred when reporting to the mining pool[{}]", e.getMessage());
            questionService.deletePoolTask(bi, m.getPromotionAddress());
            throw e;
        }
        log.info("Mining service-mining results submit: success !!!!!!!!!!!!!!!!!!!!!!");
        return DataResponse.success();
    }


}
