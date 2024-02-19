package com.synctech.statter.manager.api.controller.v1;

import com.synctech.statter.base.entity.ApplyForPromotion;
import com.synctech.statter.base.mapper.ApplyForPromotionMapper;
import com.synctech.statter.common.service.service.PromotionService;
import com.synctech.statter.common.service.service.WalletService;
import com.synctech.statter.common.service.vo.info.WalletVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Api(value = "api about pool for dapp")
@RequestMapping("v1/apply/prom")
@RestController("applyPromotionController")
public class ApplyPromotionController {

    @Autowired
    ApplyForPromotionMapper applyForPromotionMapper;

    @Autowired
    WalletService walletService;

    @Autowired
    PromotionService promotionService;

    @ApiOperation(httpMethod = "GET", value = "get the apply for upgrading to a promotion")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ApplyForPromotion.class) })
    @GetMapping("/{address}")
    public String get(
            @ApiParam(name = "address", value = "wallet address", type = "String", required = true) @PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        WalletVo wallet = walletService.findByAddress(address);
        ApplyForPromotion applyForPromotion = applyForPromotionMapper.find(address);
        if (null == applyForPromotion)
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_WALLET_APPLY_FOR_PROMOTION_NOT_FOUND));
        return DataResponse.success(applyForPromotion);
    }

    @ApiOperation(httpMethod = "POST", value = "commit the apply for upgrading to a promotion")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
    @PostMapping("")
    public String apply(
            @ApiParam(name = "params", type = "ApplyForPromotion", required = true) @RequestBody() ApplyForPromotion body) {
        if (null == body || StringUtils.isBlank(body.getAddress()) || StringUtils.isBlank(body.getAlias())) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        String address = body.getAddress();
        WalletVo wallet = walletService.findByAddress(address);
        ApplyForPromotion applyForPromotion = applyForPromotionMapper.find(address);
        if (null != applyForPromotion)
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_WALLET_APPLY_FOR_PROMOTION_EXIST));
        applyForPromotionMapper.add(body);
        return DataResponse.success();
    }

    @ApiOperation(httpMethod = "PUT", value = "edit the apply for upgrading to a promotion")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
    @PutMapping("")
    public String edit(
            @ApiParam(name = "params", type = "ApplyForPromotion", required = true) @RequestBody() ApplyForPromotion body) {
        if (null == body || StringUtils.isBlank(body.getAddress()) || StringUtils.isBlank(body.getAlias())) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        String address = body.getAddress();
        WalletVo wallet = walletService.findByAddress(address);
        ApplyForPromotion applyForPromotion = applyForPromotionMapper.find(address);
        if (null == applyForPromotion)
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_WALLET_APPLY_FOR_PROMOTION_EXIST));
        else if (ApplyForPromotion.Status.PASS.compare(applyForPromotion.getStatus()))
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        if (StringUtils.isNotBlank(body.getAlias()))
            applyForPromotion.setAlias(body.getAlias());
        if (StringUtils.isNotBlank(body.getIntroduction()))
            applyForPromotion.setIntroduction(body.getIntroduction());
        applyForPromotionMapper.updateInfo(body);
        return DataResponse.success();
    }

}
