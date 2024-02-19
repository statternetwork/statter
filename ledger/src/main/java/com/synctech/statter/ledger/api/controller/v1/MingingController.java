package com.synctech.statter.ledger.api.controller.v1;

import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.common.pool.service.PoolService;
import com.synctech.statter.common.pool.vo.MiningReportReq;
import com.synctech.statter.common.pool.vo.PoolTask;
import com.synctech.statter.common.service.service.MinerService;
import com.synctech.statter.common.service.service.PromotionService;
import com.synctech.statter.common.service.service.QuestionService;
import com.synctech.statter.common.service.service.WhiteService;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.ledger.api.controller.v1.vo.WorkloadDto;
import com.synctech.statter.ledger.api.service.LedgerService;
import com.synctech.statter.ledger.api.service.QuestionAnalyzeService;
import com.synctech.statter.util.JSONUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "mining: task")
@RequestMapping("statter/ledger/api/v1/mining")
@RestController
public class MingingController {

    static long reportSuccessBlockIndex = 0;

    @Autowired
    MinerService minerService;

    @Autowired
    PromotionService promotionService;

    @Autowired
    QuestionService questionService;
    @Autowired
    QuestionAnalyzeService questionAnalyzeService;

    @Autowired
    LedgerService ledgerService;

    @Autowired
    PoolService poolService;

    @Autowired
    WhiteService whiteService;

    @Operation(method = "GET", description = "query block index", responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))))
    @GetMapping("/index")
    public String index() {
        long bi = questionService.getBlockIndex();
        return DataResponse.success(bi);
    }

    @Operation(method = "GET", description = "query mining task", parameters = @Parameter(name = "sn", description = "miner sn", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PoolTask.class))))
    @GetMapping("/task/{sn}")
    public String task(@PathVariable("sn") String sn) {
        MinerVo m = minerService.findBySn(sn);
        if (null == m) {
            log.error(HttpStatusExtend.ERROR_MINER_NOT_FOUND.getReasonPhrase(), sn);
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        } else if (StringUtils.isBlank(m.getWalletAddress()) || StringUtils.isBlank(m.getPromotionAddress())) {
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST));
        } else if (!m.isCanMining()) {// the miner with this sn cannot do mining
            log.warn("{} : sn = {}", HttpStatusExtend.ERROR_MINING_MINER_CANNOT_MINING.getReasonPhrase(), m.getSn());
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_MINING_MINER_CANNOT_MINING));
        }
        // To mine the mining machine, you need to receive the task first. Here you can
        // verify the legitimacy of the equipment to avoid the useless work that can be
        // submitted.
        long bi = questionService.getBlockIndex();
        Promotion p = promotionService.get(m.getPromotionAddress());
        PoolTask pt = null;
        try {
            pt = questionService.getPoolTask();// The registration address is the promotion address. The wallet on the
                                               // mining machine will be recorded once during the correct submission to
                                               // increase the expansion
        } catch (AppBizException e) {
            if (HttpStatusExtend.ERROR_POOL_GET_POOL_TASK_NOT_MATCH_BLOCK_INDEX.value() != e.getCode())
                log.warn(e.getMessage());
            return DataResponse.fail(e);
        }
        String workload = questionAnalyzeService.analyzeWorkload(pt.getWorkload());
        pt.setWorkload(workload);
        return DataResponse.success(pt);
    }

    @GetMapping("/task/mirror")
    public String task() {
        PoolTask pt = questionService.getPoolTask();
        String workload = questionAnalyzeService.analyzeWorkload(pt.getWorkload());
        pt.setWorkload(workload);
        return DataResponse.success(pt);
    }

    @Operation(method = "PUT", description = "report mining workload", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "mining workload", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkloadDto.Req.class))), responses = @ApiResponse(content = @Content(mediaType = "application/json")))
    @PutMapping("/workload")
    public String workload(@RequestBody WorkloadDto.Req req) {
        if (null == req || !req.validate()) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        long cbi = questionService.getBlockIndex();
        if (cbi != req.getBi()) {
            return DataResponse.fail(HttpStatusExtend.ERROR_MINING_REPORT_WRONG_BLOCK_INDEX + "[" + cbi + "]");
        }
        MinerVo m = minerService.findBySn(req.getSn());
        if (null == m) {
            log.error(HttpStatusExtend.ERROR_MINER_NOT_FOUND.getReasonPhrase(), req.getSn());
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        } else if (StringUtils.isBlank(m.getWalletAddress()) || StringUtils.isBlank(m.getPromotionAddress())) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        } else if (!m.isCanMining()) {
            log.warn("{} : sn = {}", HttpStatusExtend.ERROR_MINING_MINER_CANNOT_MINING.getReasonPhrase(), m.getSn());
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        ledgerService.count(req.getBi(), req.getSn(), m.getPromotionAddress(), req.getC());
        return DataResponse.success();
    }

    @Operation(method = "POST", description = "report mining result", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "mining result content", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MiningReportReq.class))), responses = @ApiResponse(content = @Content(mediaType = "application/json")))
    @PostMapping("/report")
    public String report(@RequestBody MiningReportReq req) {
        if (null == req || !req.validate()) {
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        MinerVo m = minerService.findBySn(req.getSn());
        if (null == m) {
            log.error(HttpStatusExtend.ERROR_MINER_NOT_FOUND.getReasonPhrase(), req.getSn());
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        } else if (StringUtils.isBlank(m.getWalletAddress()) || StringUtils.isBlank(m.getPromotionAddress())) {
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        } else if (!m.isCanMining()) {
            log.warn("{} : sn = {}", HttpStatusExtend.ERROR_MINING_MINER_CANNOT_MINING.getReasonPhrase(), m.getSn());
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        long bi = questionService.getBlockIndex();
        if (bi != req.getBlockIndex()) {
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_MINING_REPORT_WRONG_BLOCK_INDEX));
        }
        Promotion p = promotionService.get(m.getPromotionAddress());
        PoolTask pt = questionService.getPoolTask();
        if (!req.compare(pt)) {
            AppBizException e = new AppBizException(HttpStatusExtend.ERROR_MINING_REPORT_WRONG_QUESTION);
            log.warn("report not compare[{}]: {}", e.getMessage(), JSONUtils.toJson(pt));
            return DataResponse.fail(e);
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // The task is legal, and the computing process is legal, so the calculation of
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// the
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// mining
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// machine,
        // whether the result is correct or not, needs to be recorded.
        ledgerService.count(bi, m.getSn(), m.getPromotionAddress(), req.getCountTimes());

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (!pt.validate(req.getRandomNumber())) {// Verify whether the calculation results are correct
            log.debug("Mining service-mining results verify failed: randomNumber={}", req.getRandomNumber());
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_MINING_REPORT_WRONG_COMPUTE_RESULT));
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Extracting the current block height of the ledger is equivalent to taking out
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// a
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// copy
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// of
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// the
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// ledger
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// and
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// generating
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// a
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// copy
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// of
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// the
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// ledger
        LedgerService.LedgerExractDto dto = ledgerService.extract(bi, m.getSn(), m.getWalletAddress(),
                m.getPromotionAddress());
        req.setPid(dto.getId());
        req.setWalletAddress(m.getPromotionAddress());// In the method of mining pool, report the mining results to the
                                                      // gateway
        try {
            poolService.commitPoolTaskUrl(req);
        } catch (AppBizException e) {
            log.error(
                    "Mining service-mining results submitted: mining service verification passed, but errors occurred when reporting to the mining pool: {}",
                    e.getMessage());
            // questionService.deletePoolTask(bi, m.getPromotionAddress());
            throw e;
        }
        if (req.getBlockIndex() > reportSuccessBlockIndex) {
            log.info("Mining service-mining results submit: success !!!!!!!!!!!!!!!!!!!!!! [bi={}] [sn={}] [p={}({})]",
                    req.getBlockIndex(), req.getSn(), p.getAddress(), p.getAlias());
            reportSuccessBlockIndex = req.getBlockIndex();
        }
        return DataResponse.success();
    }

}
