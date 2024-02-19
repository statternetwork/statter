package com.synctech.statter.mining.pool.api.controller.v1.ledger;

import cn.hutool.core.date.StopWatch;
import com.synctech.statter.base.entity.Ledger;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.base.mapper.BlockCrawlerMapper;
import com.synctech.statter.base.mapper.PromotionLedgerMapper;
import com.synctech.statter.common.service.service.QuestionService;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.mining.pool.api.controller.v1.CommonController;
import com.synctech.statter.mining.pool.api.controller.v1.ledger.vo.LedgerVo;
import com.synctech.statter.mining.pool.api.controller.v1.ledger.vo.PageLedger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "mining pool: ledger")
@RequestMapping("statter/mining/pool/api/v1/ledger")
@RestController()
public class LedgerController extends CommonController {

    @Autowired
    QuestionService questionService;

    @Autowired
    PromotionLedgerMapper promotionLedgerMapper;

    @Autowired
    BlockCrawlerMapper blockCrawlerMapper;

    @Value("${statter.promotion.ledger.cache.size:1000}")
    private Integer promotionLedgerCacheSize;

    @Operation(method = "GET", description = "", parameters = @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = LedgerVo.class))))
    @GetMapping("/last")
    public String last() {
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress()))
            return "";// exist no table
        List<Ledger> ledgerList = promotionLedgerMapper.findLimit(getPromotionAddress(), 1);
        if (CollectionUtils.isEmpty(ledgerList))
            return "";
        return DataResponse.success(ledger2Vo(ledgerList.get(0)));
    }

    @Operation(method = "GET", description = "Query the ledger with the specifying the block index", parameters = {
            @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true),
            @Parameter(name = "bi", in = ParameterIn.PATH, description = "wallet address", schema = @Schema(implementation = Integer.class), required = true)
    }, responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = LedgerVo.class))))
    @GetMapping("/get/{bi}")
    public String get(@PathVariable("bi") long bi) {
        Promotion p = super.getPromotion();
        if (bi < 0) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_WRONG_BLOCKINDEX_QUERY_LEDGER);
        }
        long curIndex = blockCrawlerMapper.max();// the processed block index
        if (bi >= (curIndex - 15)) { // the promotion can query less than "block index - 15", because the block
                                     // crawler index maybe in analyze
            return promotionWarn(p.getAddress(), HttpStatusExtend.ERROR_POOL_API_THE_BLOCK_IS_IN_PRODUCT);
            // } else if (bi == curIndex) {
            // return promotionWarn(p.getAddress(),
            // HttpStatusExtend.ERROR_POOL_API_THE_BLOCK_IS_IN_PRODUCT);
        } else if (1 != promotionLedgerMapper.existTable(getPromotionAddress())) { // no earning
            return promotionWarn(p.getAddress(), HttpStatusExtend.ERROR_POOL_API_BLOCK_NOT_BELONG_PROMOTION);
        }
        Ledger ledger = promotionLedgerMapper.findByBlockIndex(p.getAddress(), bi);
        if (null == ledger) {
            // if (promotionLedgerMapper.findCacheByBlockIndex(bi) > 0) {
            // return promotionWarn(p.getAddress(),
            // HttpStatusExtend.ERROR_POOL_API_THE_BLOCK_IS_IN_PRODUCT);
            // } else {
            return promotionWarn(p.getAddress(), HttpStatusExtend.ERROR_POOL_API_BLOCK_NOT_BELONG_PROMOTION);
            // }
        }
        return DataResponse.success(ledger2Vo(ledger));
    }

    private String promotionWarn(String promotionAddress, HttpStatusExtend warn) {
        log.warn("promotion[{}] warn: {}", promotionAddress, warn.getReasonPhrase());
        return DataResponse.fail(warn);
    }

    /**
     * @return
     */
    @Operation(method = "GET", description = "Query the latest one thousand ledgers", parameters = @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LedgerVo.class)))))
    @GetMapping("/last/thousand")
    public String promotionLastThousand() {
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress()))
            return DataResponse.success(new ArrayList<>());// exist no table
        StopWatch sw = new StopWatch("promotionLastThousand");
        sw.start("get promotion ledger list");
        List<Ledger> list = promotionLedgerMapper.findLimit(getPromotionAddress(), promotionLedgerCacheSize);
        sw.stop();
        sw.start("convert data");
        List<LedgerVo> r = new ArrayList<>();
        for (Ledger l : list) {
            r.add(ledger2Vo(l));
        }
        sw.stop();
        log.info(sw.prettyPrint());
        return DataResponse.success(r);
    }

    @Operation(method = "GET", description = "Query the ledger with the specifying the blockindex area, the discrepancy between ei-si must be less than 1000 ", parameters = {
            @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true),
            @Parameter(name = "si", in = ParameterIn.PATH, schema = @Schema(implementation = Integer.class), required = true)
    }, responses = @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LedgerVo.class)))))
    @GetMapping("/list/{si}/{ei}")
    public String listByArea(@PathVariable("si") long si, @PathVariable("ei") long ei) {
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress()))
            return DataResponse.success(new ArrayList<>());// exist no table
        Promotion p = super.getPromotion();
        if (si < 0 || ei < si || ei - si > 1000) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_WRONG_BLOCKINDEX_QUERY_LEDGER);
        }
        long curIndex = questionService.getBlockIndex();
        if (si >= curIndex || ei >= curIndex) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_BLOCKINDEX_IS_CURRENT);
        }
        List<Ledger> ledgerList = promotionLedgerMapper.findByBlockIndexArea(p.getAddress(), si, ei);
        List<LedgerVo> r = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ledgerList)) {
            ledgerList.forEach(l -> r.add(ledger2Vo(l)));
        }
        return DataResponse.success(r);
    }

    @Operation(method = "GET", description = "Page query the ledger, order by the block index with 'ASC'", parameters = {
            @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true),
            @Parameter(name = "page", description = "page num, the start is 1.", in = ParameterIn.PATH, schema = @Schema(implementation = Integer.class), required = true),
            @Parameter(name = "e", description = "page size, max 100.", in = ParameterIn.PATH, schema = @Schema(implementation = Integer.class), required = true)
    }, responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageLedger.Resp.class))))
    @GetMapping("/page/{page}/{size}")
    public String page(@PathVariable("page") int page, @PathVariable("size") int size) {
        StopWatch w = new StopWatch("page query ledger");
        if (page < 1 || size > 100)
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_INVALID_PAGE_NUM_OR_SIZE);
        PageLedger.Resp r = new PageLedger.Resp().setPage(page).setSize(size);
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress()))
            return DataResponse.success(r);// exist no table
        w.start("count total");
        long total = promotionLedgerMapper.count(getPromotionAddress());
        w.stop();
        r.setTotal(total);
        if (total == 0)
            return DataResponse.success(r);
        long si = (page - 1) * size;
        long ei = page * size;
        w.start("query data");
        List<Ledger> ledgers = promotionLedgerMapper.page(getPromotionAddress(), si, ei);
        w.stop();
        List<LedgerVo> list = ledgers.stream().map(l -> ledger2Vo(l)).collect(Collectors.toList());
        r.setData(list);
        log.info(w.prettyPrint());
        return DataResponse.success(r);
    }

    private LedgerVo ledger2Vo(Ledger l) {
        Map<String, Object> data = com.alibaba.fastjson.JSONObject.parseObject(l.getData());
        data.forEach((k, v) -> {
            String s = (String) v;
            if (StringUtils.contains(s, "_"))
                s = s.split("_")[0];
            data.put(k, s);
        });
        LedgerVo vo = new LedgerVo().setBi(l.getBlockIndex()) // block index
                .setSn(l.getSn()) // the sn of the productor machine
                .setA(l.getAddress()) // the wallet address of the productor machine
                .setP(l.getMingProfit()) // tag ming profit
                .setData(data); // the compute count ledger of this block
        return vo;
    }

}
