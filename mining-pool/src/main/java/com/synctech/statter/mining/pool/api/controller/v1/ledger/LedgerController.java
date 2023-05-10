package com.synctech.statter.mining.pool.api.controller.v1.ledger;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.synctech.statter.base.entity.Ledger;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.base.mapper.LedgerMapper;
import com.synctech.statter.base.mapper.PromotionLedgerMapper;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.mining.pool.api.controller.v1.CommonController;
import com.synctech.statter.mining.pool.api.controller.v1.ledger.vo.LedgerVo;
import com.synctech.statter.mining.pool.api.controller.v1.ledger.vo.PageLedger;
import com.synctech.statter.redis.jedis.JedisService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(value = "api of the ledger")
@RequestMapping("v1/ledger")
@RestController()
public class LedgerController extends CommonController {

    @Resource
    JedisService jedisService;

    @Resource
    PromotionLedgerMapper promotionLedgerMapper;

    @ApiOperation(httpMethod = "GET", value = "")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = LedgerVo.class)})
    @GetMapping("/last")
    public String last() {
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress())) return "";// exist no table
        List<Ledger> ledgerList = promotionLedgerMapper.findLimit(getPromotionAddress(), 1);
        if (CollectionUtils.isEmpty(ledgerList)) return "";
        return DataResponse.success(ledger2Vo(ledgerList.get(0)));
    }

    @ApiOperation(httpMethod = "GET", value = "Query the ledger with the specifying the blockindex")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = LedgerVo.class)})
    @GetMapping("/get/{bi}")
    public String get(@ApiParam(value = "block index", type = "long", required = true) @PathVariable("bi") long bi) {
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress())) return DataResponse.success();// exist no table
        Promotion p = super.getPromotion();
        if (bi < 0) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_WRONG_BLOCKINDEX_QUERY_LEDGER);
        }
        int curIndex = Integer.valueOf(jedisService.get(CacheKey.CACHEKEY_MINING_BLOCK_INDEX));
        if (bi >= curIndex) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_WRONG_BLOCKINDEX_QUERY_LEDGER);
        }
        Ledger ledger = promotionLedgerMapper.findByBlockIndex(p.getAddress(), bi);
        if (null == ledger) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_BLOCK_NOT_BELONG_PROMOTION);
        }
        return DataResponse.success(ledger2Vo(ledger));
    }

    /**
     * @return
     */
    @ApiOperation(httpMethod = "GET", value = "Query the latest one thousand ledgers")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = LedgerVo[].class)})
    @GetMapping("/last/thousand")
    public String promotionLastThousand() {
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress())) return DataResponse.success(new ArrayList<>());// exist no table
        StopWatch sw = new StopWatch("promotionLastThousand");
        sw.start("get promotion ledger list cache");
        String v = jedisService.hget(CacheKey.CACHEKEY_LEDGER_LIST_BY_PROMOTION, getPromotionAddress());
        sw.stop();
        List<LedgerVo> r = new ArrayList<>();
        if (StringUtils.isBlank(v)) {
            log.info(sw.prettyPrint());
            return DataResponse.success(r);
        }
        sw.start("convert data");
        JSONArray arr = new JSONArray(v);
        for (int i = 0; i < arr.size(); i++) {
            Ledger l = arr.get(i, Ledger.class);
            r.add(ledger2Vo(l));
        }
        sw.stop();
        log.info(sw.prettyPrint());
        return DataResponse.success(r);
    }

    @ApiOperation(httpMethod = "GET", value = "Query the ledger with the specifying the blockindex area, the discrepancy between ei-si must be less than 1000 ")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = LedgerVo[].class)})
    @GetMapping("/list/{si}/{ei}")
    public String listByArea(@ApiParam(value = "wallet address", type = "long", required = true) @PathVariable("si") long si, @ApiParam(value = "wallet address", type = "long", required = true) @PathVariable("ei") long ei) {
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress())) return DataResponse.success(new ArrayList<>());// exist no table
        Promotion p = super.getPromotion();
        if (si < 0 || ei < si || ei - si > 1000) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_WRONG_BLOCKINDEX_QUERY_LEDGER);
        }
        int curIndex = Integer.valueOf(jedisService.get(CacheKey.CACHEKEY_MINING_BLOCK_INDEX));
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

    @ApiOperation(httpMethod = "GET", value = "Page query the ledger, order by the block index with 'ASC'")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = LedgerVo[].class)})
    @GetMapping("/page/{page}/{size}")
    public String page(@ApiParam(value = "page num, the start is 1.", type = "int", required = true) @PathVariable("page") int page,
                       @ApiParam(value = "page size, max 100.", type = "int", required = true) @PathVariable("size") int size) {
        StopWatch w = new StopWatch("page query ledger");
        if (page < 1 || size > 100) throw new AppBizException(HttpStatusExtend.ERROR_POOL_API_INVALID_PAGE_NUM_OR_SIZE);
        PageLedger.Resp r = new PageLedger.Resp().setPage(page).setSize(size);
        if (1 != promotionLedgerMapper.existTable(getPromotionAddress())) return DataResponse.success(r);// exist no table
        w.start("count total");
        long total = promotionLedgerMapper.count(getPromotionAddress());
        w.stop();
        r.setTotal(total);
        if (total == 0) return DataResponse.success(r);
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
        LedgerVo vo = new LedgerVo().setBi(l.getBlockIndex()) // block index
                .setSn(l.getSn()) // the sn of the productor machine
                .setA(l.getAddress()) // the wallet address of the productor machine
                .setP(l.getMingProfit())
                .setData(new JSONObject(l.getData())); // the compute count ledger of this block
        return vo;
    }


}
