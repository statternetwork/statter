package com.statter.statter.manager.api.controller.v1;

import com.statter.statter.base.entity.Promotion;
import com.statter.statter.base.entity.Wallet;
import com.statter.statter.common.service.service.*;
import com.statter.statter.common.service.vo.info.MinerVo;
import com.statter.statter.common.service.vo.info.WalletVo;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.constant.restful.DataResponse;
import com.statter.statter.manager.api.controller.v1.vo.WalletApiVo;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "manager: wallet")
@RequestMapping("statter/manager/api/v1/wallet")
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

    @Operation(method = "GET", description = "query wallet info", parameters = @Parameter(name = "wa", description = "wallet address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = WalletVo.class))))
    @GetMapping("/{wa}")
    public String get(@PathVariable("wa") String wa) {
        WalletVo w = walletService.findByAddress(wa);
        return DataResponse.success(w);
    }

    @Operation(method = "GET", description = "Obtain the list of mining machines according to the wallet address", parameters = @Parameter(name = "address", description = "wallet address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MinerVo.class)))))
    @GetMapping("/list/miner/{address}")
    public String listMinerByWallet(@PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        List<MinerVo> list = minerService.findByWallet(address);
        // list.forEach(vo -> hashService.queryMiner(vo));
        return DataResponse.success(list);
    }

    @Operation(method = "POST", description = "wallet select the promotion", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "wallet info", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Wallet.class))))
    @PostMapping("/promotion")
    public String selectPromotion(@RequestBody @Validated Wallet w) {
        log.debug("wallet select the promotion");
        if (StringUtils.isBlank(w.getAddress()) ||
                StringUtils.isBlank(w.getPromotionAddress()))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        Wallet wpo = walletService.findByAddress(w.getAddress());
        if (StringUtils.isNotBlank(wpo.getPromotionAddress()))
            throw new AppBizException(HttpStatusExtend.ERROR_WALLET_SELECTED_PROMOTION_EXIST);
        Promotion p = promotionService.get(w.getPromotionAddress());
        if (null == p)
            throw new AppBizException(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
        walletService.updateWalletPromotion(wpo.getAddress(), p.getAddress());
        return DataResponse.success();
    }

    @Operation(method = "PUT", description = "rename alias", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = WalletApiVo.RenameReq.class))))
    @PutMapping("/rename")
    public String rename(@RequestBody @Validated WalletApiVo.RenameReq r) {
        if (StringUtils.isBlank(r.getAddress()) || StringUtils.isBlank(r.getAlias()))
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        Wallet wpo = walletService.findByAddress(r.getAddress());
        wpo.setAlias(r.getAlias());
        walletService.update(wpo);
        return DataResponse.success();
    }

}
