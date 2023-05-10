package com.synctech.statter.mining.pool.api.controller.v1.promotion;

import cn.hutool.core.bean.BeanUtil;
import com.synctech.statter.base.entity.Miner;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.mining.pool.api.controller.v1.CommonController;
import com.synctech.statter.mining.pool.api.controller.v1.promotion.vo.MinerSimpleVo;
import com.synctech.statter.mining.pool.api.controller.v1.promotion.vo.PromotionSimpleVo;
import com.synctech.statter.mining.pool.api.controller.v1.promotion.vo.WalletSimpleVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(value = "api of the promotion")
@RequestMapping("v1/prom")
@RestController()
public class PromotionController extends CommonController {

    @ApiOperation(httpMethod = "GET", value = "Obtain a list of miners (wallets) according to the promotion address")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = WalletSimpleVo[].class)})
    @GetMapping("/wallet/list")
    public String listWallet() {
        List<Wallet> ws = promotionService.listWallet(getPromotionAddress());
        List<WalletSimpleVo> r = new ArrayList<>();
        ws.forEach(w -> r.add(BeanUtil.toBean(w, WalletSimpleVo.class)));
        return DataResponse.success(r);
    }

    @ApiOperation(httpMethod = "GET", value = "Obtain a list of miner machines according to the promotion address")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = MinerSimpleVo[].class)})
    @GetMapping("/miner/list")
    public String listMiner() {
        List<Miner> ms = promotionService.listMiner(getPromotionAddress());
        List<MinerSimpleVo> r = new ArrayList<>();
        ms.forEach(m -> r.add(BeanUtil.toBean(m, MinerSimpleVo.class)));
        return DataResponse.success(r);
    }

    @ApiOperation(httpMethod = "PUT", value = "change the mine pool description")
    @ApiResponses({@ApiResponse(code = 200, message = "OK")})
    @PutMapping("")
    public String updateDescription(@ApiParam(value = "bind wallet address param", type = "json", required = true)
                                    @RequestBody @Validated PromotionSimpleVo vo) {
        if (null == vo || StringUtils.isBlank(vo.getIntroduction())) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        Promotion pro = promotionService.get(getPromotionAddress());
        pro.setAlias(vo.getAlias());
        pro.setIntroduction(vo.getIntroduction());
        promotionService.update(pro);
        return DataResponse.success();
    }

}
