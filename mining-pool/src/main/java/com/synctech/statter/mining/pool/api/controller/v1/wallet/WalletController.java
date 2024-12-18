package com.statter.statter.mining.pool.api.controller.v1.wallet;

import com.statter.statter.common.service.service.HashService;
import com.statter.statter.common.service.service.MinerService;
import com.statter.statter.common.service.service.PromotionService;
import com.statter.statter.common.service.service.WalletService;
import com.statter.statter.common.service.vo.info.MinerVo;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.DataResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "mining pool: wallet")
@RequestMapping("statter/mining/pool/api/v1/wallet")
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

    /*
     * @ApiOperation(httpMethod = "POST", value = "wallet join the promotion")
     * 
     * @ApiResponses({@ApiResponse(code = 200, message = "OK", response =
     * JoinPromotion.Resp.class)})
     * 
     * @PostMapping("/join/promotion")
     * public String joinPromotion(@ApiParam(value = "info", type = "json", required
     * = true) @RequestBody @Validated JoinPromotion.Req req) {
     * log.debug("wallet select the promotion");
     * if (StringUtils.isBlank(req.getAddress()) ||
     * StringUtils.isBlank(req.getPromotionAddress()))
     * throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
     * Wallet wpo = walletService.findByAddress(req.getAddress());
     * if (StringUtils.isNotBlank(wpo.getPromotionAddress()))
     * throw new
     * AppBizException(HttpStatusExtend.ERROR_WALLET_SELECTED_PROMOTION_EXIST);
     * Promotion p = promotionService.get(req.getPromotionAddress());
     * if (null == p) throw new
     * AppBizException(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
     * List<MinerVo> l = minerService.findByWallet(req.getAddress());
     * if (!CollectionUtils.isEmpty(l)) {
     * for (MinerVo m : l) {
     * if (StringUtils.isNotBlank(m.getPromotionAddress())) {
     * if (!StringUtils.equals(m.getPromotionAddress(), p.getAddress()))
     * throw new AppBizException(HttpStatusExtend.
     * ERROR_WALLET_SELECTED_PROMOTION_MINER_EXIST_PROMOTION);
     * }
     * }
     * for (MinerVo m : l) {
     * m.setPromotionAddress(p.getAddress());
     * minerService.updateMiner(m);
     * }
     * }
     * walletService.updateWalletPromotion(wpo.getAddress(), p.getAddress());
     * return DataResponse.success();
     * }
     */

    @Operation(method = "GET", description = "Obtain the list of mining machines according to the wallet address", parameters = {
            @Parameter(name = "sak", description = "the promotion secrect access key", in = ParameterIn.HEADER, schema = @Schema(implementation = String.class), required = true),
            @Parameter(name = "address", description = "wallet address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true)
    }, responses = @ApiResponse(content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MinerVo.class)))))
    @GetMapping("/list/miner/{address}")
    public String listMinerByWallet(@PathVariable("address") String address) {
        if (StringUtils.isBlank(address)) {
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        List<MinerVo> list = minerService.findByWallet(address);
        // list.forEach(vo -> hashService.queryMiner(vo));
        return DataResponse.success(list);
    }

}
