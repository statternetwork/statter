package com.statter.statter.manager.api.controller.v1;

import com.statter.statter.base.entity.ApplyForPromotion;
import com.statter.statter.base.entity.Promotion;
import com.statter.statter.base.mapper.ApplyForPromotionMapper;
import com.statter.statter.common.service.service.PromotionService;
import com.statter.statter.common.service.service.WalletService;
import com.statter.statter.common.service.vo.info.WalletVo;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.constant.restful.DataResponse;
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
@Tag(name = "manager: apply promotion")
@RequestMapping("statter/manager/api/v1/apply/prom")
@RestController("applyPromotionController")
public class ApplyPromotionController {

    @Autowired
    ApplyForPromotionMapper applyForPromotionMapper;

    @Autowired
    WalletService walletService;

    @Autowired
    PromotionService promotionService;

    @Operation(method = "GET", description = "get the apply for upgrading to a promotion", parameters = @Parameter(name = "address", description = "wallet address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplyForPromotion.class))))
    @GetMapping("/{address}")
    public String get(@PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        WalletVo wallet = walletService.findByAddress(address);
        ApplyForPromotion applyForPromotion = applyForPromotionMapper.find(address);
        if (null == applyForPromotion)
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_WALLET_APPLY_FOR_PROMOTION_NOT_FOUND));
        return DataResponse.success(applyForPromotion);
    }

    @Operation(method = "POST", description = "commit the apply for upgrading to a promotion", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "apply information for promotion", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplyForPromotion.class))))
    @PostMapping("")
    public String apply(@RequestBody() ApplyForPromotion body) {
        if (null == body || StringUtils.isBlank(body.getAddress()) ||
                StringUtils.isBlank(body.getAlias())) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        String address = body.getAddress();
        WalletVo wallet = walletService.findByAddress(address);
        ApplyForPromotion applyForPromotion = applyForPromotionMapper.find(address);
        if (null != applyForPromotion)
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_WALLET_APPLY_FOR_PROMOTION_EXIST));
        Promotion p = promotionService.findByAlias(body.getAlias());
        if (null != p)
            return DataResponse.fail(new AppBizException(HttpStatusExtend.ERROR_WALLET_ALIAS_FOR_PROMOTION_EXIST));
        applyForPromotionMapper.add(body);
        return DataResponse.success();
    }

    @Operation(method = "PUT", description = "edit the apply for upgrading to a promotion", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "apply information for promotion", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplyForPromotion.class))))
    @PutMapping("")
    public String edit(@RequestBody() ApplyForPromotion body) {
        if (null == body || StringUtils.isBlank(body.getAddress()) ||
                StringUtils.isBlank(body.getAlias())) {
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
