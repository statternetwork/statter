package com.synctech.statter.manager.api.controller.v1;

import cn.hutool.core.bean.BeanUtil;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.base.vo.Page;
import com.synctech.statter.common.service.service.PromotionService;
import com.synctech.statter.common.service.vo.info.WalletVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.manager.api.controller.v1.vo.PromotionInfoVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Api(value = "api about pool for dapp")
@RequestMapping("v1/prom")
@RestController("openPromotionController")
public class PromotionController {

    @Autowired
    PromotionService promotionService;

    @ApiOperation(httpMethod = "GET", value = "page query the mine pool")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = PromotionInfoVo.class) })
    @GetMapping("/page")
    public String page(
            @ApiParam(name = "page", value = "Page number. The first page is 1", type = "int", required = true) @RequestParam("page") int page,
            @ApiParam(name = "size", value = "The data size per page, max is 10", type = "int", required = true) @RequestParam("size") int size,
            @ApiParam(name = "kw", value = "key word", allowEmptyValue = true, defaultValue = "") @RequestParam("kw") String kw) {
        Page<Promotion> p = promotionService.page(page, size, kw.trim());
        List<PromotionInfoVo> d = p.getData().stream().map(t -> BeanUtil.toBean(t, PromotionInfoVo.class))
                .collect(Collectors.toList());
        Page<PromotionInfoVo> r = new Page<PromotionInfoVo>(p.getPage(), p.getSize()).setTotal(p.getTotal()).setData(d);
        return DataResponse.success(r);
    }

    @ApiOperation(httpMethod = "GET", value = "obtain the mine pool information according to the ore pool wallet address")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = PromotionInfoVo.class) })
    @GetMapping("/{address}")
    public String get(
            @ApiParam(name = "address", value = "promotion address", type = "String", required = true) @PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        }
        Promotion pro = promotionService.get(address);
        PromotionInfoVo r = BeanUtil.toBean(pro, PromotionInfoVo.class);
        // r.setAddress(pro.getAddress()).setMinerCount(pro.getMinerCount());
        long h = promotionService.getHash(address);
        r.setHash(h);
        return DataResponse.success(r);
    }

    @ApiOperation(httpMethod = "PUT", value = "change the mine pool description")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = PromotionInfoVo.class) })
    @PutMapping("/{address}")
    public String updateDescription(
            @ApiParam(name = "address", value = "promotion address", type = "String", required = true) @PathVariable("address") String address,
            @ApiParam(value = "promotion info, only use the field named `introduction` here", type = "json", required = true) @RequestBody @Validated PromotionInfoVo vo) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        } else if (null == vo || StringUtils.isBlank(vo.getIntroduction())) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        Promotion pro = promotionService.get(address);
        pro.setIntroduction(vo.getIntroduction());
        promotionService.update(pro);
        return DataResponse.success();
    }

    @ApiOperation(httpMethod = "GET", value = "Obtain a list of miners (wallets) according to the promotion of wallet address")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = WalletVo[].class) })
    @GetMapping("/list/{address}")
    public String listWallet(
            @ApiParam(name = "address", value = "promotion address", type = "String", required = true) @PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        }
        List<Wallet> ws = promotionService.listWallet(address);
        List<WalletVo> r = new ArrayList<>();
        ws.forEach(w -> r.add(BeanUtil.toBean(w, WalletVo.class)));
        return DataResponse.success(r);
    }

}
