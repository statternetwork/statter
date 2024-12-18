package com.statter.statter.mining.pool.api.controller.v1.promotion;

import cn.hutool.core.bean.BeanUtil;
import com.statter.statter.base.entity.Miner;
import com.statter.statter.base.entity.Promotion;
import com.statter.statter.base.entity.Wallet;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.DataResponse;
import com.statter.statter.mining.pool.api.controller.v1.CommonController;
import com.statter.statter.mining.pool.api.controller.v1.promotion.vo.MinerSimpleVo;
import com.statter.statter.mining.pool.api.controller.v1.promotion.vo.PromotionInformationVo;
import com.statter.statter.mining.pool.api.controller.v1.promotion.vo.PromotionSimpleVo;
import com.statter.statter.mining.pool.api.controller.v1.promotion.vo.WalletSimpleVo;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Tag(name = "mining pool: promotion")
@RequestMapping("statter/mining/pool/api/v1/prom")
@RestController()
public class PromotionController extends CommonController {

    @Operation(method = "GET", description = "get your mine pool information", parameters = @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromotionInformationVo.class))))
    @GetMapping("")
    public String get() {
        Promotion p = promotionService.get(getPromotionAddress());
        PromotionInformationVo vo = BeanUtil.toBean(p, PromotionInformationVo.class);
        vo.setTheoreticalHash(p.getHash());
        String h = jedisService.hget(CacheKey.CACHEKEY_HASH_INFO_PROMOTION, getPromotionAddress());
        vo.setRealTimeHash(StringUtils.isBlank(h) ? 0 : Long.valueOf(h));
        return DataResponse.success(vo);
    }

    @Operation(method = "GET", description = "Obtain a list of miners (wallets) according to the promotion address", parameters = @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = WalletSimpleVo.class)))))
    @GetMapping("/wallet/list")
    public String listWallet() {
        List<Wallet> ws = promotionService.listWallet(getPromotionAddress());
        List<WalletSimpleVo> r = new ArrayList<>();
        ws.forEach(w -> r.add(BeanUtil.toBean(w, WalletSimpleVo.class)));
        return DataResponse.success(r);
    }

    @Operation(method = "GET", description = "Obtain a list of miner machines according to the promotion address", parameters = @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MinerSimpleVo.class)))))
    @GetMapping("/miner/list")
    public String listMiner() {
        List<Miner> ms = promotionService.listMiner(getPromotionAddress());
        List<MinerSimpleVo> r = new ArrayList<>();
        for (Miner m : ms) {
            MinerSimpleVo vo = BeanUtil.toBean(m, MinerSimpleVo.class);
            // log.info("PromotionController#listMiner: {} ------------ {}",
            // JSONObject.toJSONString(m), JSONObject.toJSONString(vo));
            r.add(vo);
        }
        return DataResponse.success(r);
    }

    @Operation(method = "GET", description = "change the mine pool description", parameters = @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "bind wallet address param", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromotionSimpleVo.class))))
    @PutMapping("")
    public String updateDescription(@RequestBody @Validated PromotionSimpleVo vo) {
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
