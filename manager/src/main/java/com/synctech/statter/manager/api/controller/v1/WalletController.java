package com.synctech.statter.manager.api.controller.v1;

import cn.hutool.core.bean.BeanUtil;
import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.common.service.service.*;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.common.service.vo.info.WalletVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.manager.api.controller.v1.vo.WalletApiVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Slf4j
@Api(value = "api about wallet control")
@RequestMapping("v1/wallet")
@RestController("openWalletController")
public class WalletController {

    @Autowired
    MinerService minerService;

    @Autowired
    WalletService walletService;

    @Autowired
    PromotionService promotionService;

    @Autowired
    HashService hashService;

    @Autowired
    ProcessService processService;

    @ApiOperation(httpMethod = "GET", value = "query wallet info")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = WalletVo.class) })
    @GetMapping("/{wa}")
    public String get(
            @ApiParam(name = "wa", value = "wallet address", type = "String", required = true) @PathVariable("wa") String wa) {
        Wallet w = walletService.findByAddress(wa);
        WalletVo vo = BeanUtil.toBean(w, WalletVo.class);
        return DataResponse.success(vo);
    }

    @ApiOperation(httpMethod = "GET", value = "Obtain the list of mining machines according to the wallet address")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = MinerVo[].class) })
    @GetMapping("/list/miner/{address}")
    public String listMinerByWallet(
            @ApiParam(name = "address", value = "wallet address", type = "String", required = true) @PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        List<MinerVo> list = minerService.findByWallet(address);
        list.forEach(vo -> hashService.queryMiner(vo));
        return DataResponse.success(list);
    }

    @ApiOperation(httpMethod = "POST", value = "wallet select the promotion")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
    @PostMapping("/promotion")
    public String selectPromotion(
            @ApiParam(value = "info", type = "json", required = true) @RequestBody @Validated Wallet w) {
        log.debug("wallet select the promotion");
        if (StringUtils.isBlank(w.getAddress()) || StringUtils.isBlank(w.getPromotionAddress()))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        Wallet wpo = walletService.findByAddress(w.getAddress());
        if (StringUtils.isNotBlank(wpo.getPromotionAddress()))
            throw new AppBizException(HttpStatusExtend.ERROR_WALLET_SELECTED_PROMOTION_EXIST);
        Promotion p = promotionService.get(w.getPromotionAddress());
        if (null == p)
            throw new AppBizException(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        List<MinerVo> l = minerService.findByWallet(w.getAddress());
        if (!CollectionUtils.isEmpty(l)) {
            for (MinerVo m : l) {
                if (StringUtils.isNotBlank(m.getPromotionAddress())) {
                    if (!StringUtils.equals(m.getPromotionAddress(), p.getAddress()))
                        throw new AppBizException(
                                HttpStatusExtend.ERROR_WALLET_SELECTED_PROMOTION_MINER_EXIST_PROMOTION);
                }
            }
            for (MinerVo m : l) {
                m.setPromotionAddress(p.getAddress());
                minerService.updateMiner(m);
            }
        }
        walletService.updateWalletPromotion(wpo.getAddress(), p.getAddress());
        return DataResponse.success();
    }

    @ApiOperation(httpMethod = "PUT", value = "rename alias")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
    @PutMapping("/rename")
    public String rename(
            @ApiParam(value = "info", type = "json", required = true) @RequestBody @Validated WalletApiVo.RenameReq r) {
        if (StringUtils.isBlank(r.getAddress()) || StringUtils.isBlank(r.getAlias()))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        Wallet wpo = walletService.findByAddress(r.getAddress());
        wpo.setAlias(r.getAlias());
        walletService.update(wpo);
        return DataResponse.success();
    }

}
