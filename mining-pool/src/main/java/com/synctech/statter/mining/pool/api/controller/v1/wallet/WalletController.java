package com.synctech.statter.mining.pool.api.controller.v1.wallet;

import com.synctech.statter.base.entity.Promotion;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.common.service.service.HashService;
import com.synctech.statter.common.service.service.MinerService;
import com.synctech.statter.common.service.service.PromotionService;
import com.synctech.statter.common.service.service.WalletService;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.mining.pool.api.controller.v1.wallet.vo.JoinPromotion;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Api(value = "api about wallet control")
@RequestMapping("v1/wallet")
@RestController("openWalletController")
public class WalletController {

    @Resource
    MinerService minerService;

    @Resource
    WalletService walletService;

    @Resource
    PromotionService promotionService;

    @Resource
    HashService hashService;

    /*@ApiOperation(httpMethod = "POST", value = "wallet join the promotion")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = JoinPromotion.Resp.class)})
    @PostMapping("/join/promotion")
    public String joinPromotion(@ApiParam(value = "info", type = "json", required = true) @RequestBody @Validated JoinPromotion.Req req) {
        log.debug("wallet select the promotion");
        if (StringUtils.isBlank(req.getAddress()) || StringUtils.isBlank(req.getPromotionAddress()))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        Wallet wpo = walletService.findByAddress(req.getAddress());
        if (StringUtils.isNotBlank(wpo.getPromotionAddress()))
            throw new AppBizException(HttpStatusExtend.ERROR_WALLET_SELECTED_PROMOTION_EXIST);
        Promotion p = promotionService.get(req.getPromotionAddress());
        if (null == p) throw new AppBizException(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        List<MinerVo> l = minerService.findByWallet(req.getAddress());
        if (!CollectionUtils.isEmpty(l)) {
            for (MinerVo m : l) {
                if (StringUtils.isNotBlank(m.getPromotionAddress())) {
                    if (!StringUtils.equals(m.getPromotionAddress(), p.getAddress()))
                        throw new AppBizException(HttpStatusExtend.ERROR_WALLET_SELECTED_PROMOTION_MINER_EXIST_PROMOTION);
                }
            }
            for (MinerVo m : l) {
                m.setPromotionAddress(p.getAddress());
                minerService.updateMiner(m);
            }
        }
        walletService.updateWalletPromotion(wpo.getAddress(), p.getAddress());
        return DataResponse.success();
    }*/

    @ApiOperation(httpMethod = "GET", value = "Obtain the list of mining machines according to the wallet address")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = MinerVo[].class)})
    @GetMapping("/list/miner/{address}")
    public String listMinerByWallet(@ApiParam(name = "address", value = "wallet address", type = "String", required = true) @PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        List<MinerVo> list = minerService.findByWallet(address);
        list.forEach(vo -> hashService.queryMiner(vo));
        return DataResponse.success(list);
    }

}
