package com.synctech.statter.manager.api.controller.v1;

import cn.hutool.core.bean.BeanUtil;
import com.synctech.statter.base.entity.Miner;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.base.vo.Page;
import com.synctech.statter.common.service.service.MinerService;
import com.synctech.statter.common.service.service.PromotionService;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.common.service.vo.info.WalletVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.manager.api.controller.v1.vo.PromotionInfoVo;
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
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "manager: promotion")
@RequestMapping("statter/manager/api/v1/prom")
@RestController("openPromotionController")
public class PromotionController {

    @Autowired
    PromotionService promotionService;

    @Autowired
    MinerService minerService;

    @Operation(method = "GET", description = "page query the mine pool", parameters = {
            @Parameter(name = "page", description = "Page number. The first page is 1", schema = @Schema(implementation = Integer.class), required = true),
            @Parameter(name = "size", description = "The data size per page, max is 10", schema = @Schema(implementation = Integer.class), required = true),
            @Parameter(name = "kw", description = "key word", schema = @Schema(implementation = String.class), required = true)
    }, responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))))
    @GetMapping("/page")
    public String page(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("kw") String kw) {
        Page<Promotion> p = promotionService.page(page, size, kw.trim());
        if (!CollectionUtils.isEmpty(p.getData())) {
            for (Promotion d : p.getData()) {
                d.setHash(0);
            }
        }
        List<PromotionInfoVo> d = p.getData().stream().map(t -> BeanUtil.toBean(t,
                PromotionInfoVo.class)).collect(Collectors.toList());
        Page<PromotionInfoVo> r = new Page<PromotionInfoVo>(p.getPage(),
                p.getSize()).setTotal(p.getTotal()).setData(d);
        return DataResponse.success(r);
    }

    @Operation(method = "GET", description = "obtain the mine pool information according to the ore pool wallet address", parameters = @Parameter(name = "address", description = "promotion address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromotionInfoVo.class))))
    @GetMapping("/{address}")
    public String get(@PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        }
        Promotion pro = promotionService.get(address);
        if (pro.getStatus() != 1)
            throw new AppBizException(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        PromotionInfoVo r = BeanUtil.toBean(pro, PromotionInfoVo.class);
        // r.setAddress(pro.getAddress()).setMinerCount(pro.getMinerCount());
        long h = promotionService.getHash(address);
        r.setHash(h);
        return DataResponse.success(r);
    }

    @Operation(method = "PUT", description = "change the mine pool description", parameters = @Parameter(name = "address", description = "promotion address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "promotion info, only use the field named `introduction` here", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromotionInfoVo.class))))
    @PutMapping("/{address}")
    public String updateDescription(@PathVariable("address") String address,
            @RequestBody @Validated PromotionInfoVo vo) {
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

    @Operation(method = "GET", description = "Obtain a list of wallet (wallets) according to the promotion of wallet address", parameters = @Parameter(name = "address", description = "promotion address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = WalletVo.class)))))
    @GetMapping("/list/{address}")
    public String listWallet(@PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        }
        List<Wallet> ws = promotionService.listWallet(address);
        List<WalletVo> r = new ArrayList<>();
        ws.forEach(w -> r.add(BeanUtil.toBean(w, WalletVo.class)));
        return DataResponse.success(r);
    }

    @Operation(method = "GET", description = "Obtain a list of miner machines", parameters = @Parameter(name = "address", description = "promotion address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = WalletVo.class)))))
    @GetMapping("/list/machine/{address}")
    public String listMiner(@PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        }
        List<Miner> ms = promotionService.listMiner(address);
        List<MinerVo> r = new ArrayList<>();
        ms.forEach(m -> {
            MinerVo vo = minerService.findBySn(m.getSn());
            if (null != vo)
                r.add(vo);
        });
        return DataResponse.success(r);
    }

}
