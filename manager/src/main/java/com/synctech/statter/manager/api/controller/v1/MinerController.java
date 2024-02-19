package com.synctech.statter.manager.api.controller.v1;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Miner;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.base.mapper.MinerMapper;
import com.synctech.statter.common.service.service.HashService;
import com.synctech.statter.common.service.service.MinerService;
import com.synctech.statter.common.service.service.RuleService;
import com.synctech.statter.common.service.service.WalletService;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.manager.api.controller.v1.vo.ReqMinerInfo;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "manager: miner")
@RequestMapping("statter/manager/api/v1/miner")
@RestController("openMinerController")
public class MinerController {

    @Autowired
    MinerService minerService;
    @Autowired
    MinerMapper minerMapper;

    @Autowired
    WalletService walletService;

    @Autowired
    RuleService ruleService;

    @Autowired
    HashService hashService;

    @GetMapping("")
    public String get(@RequestParam String sn) {
        return DataResponse.success(minerService.findBySn(sn));
    }

    @Operation(method = "POST", description = "query machine info", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "basic machine param", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReqMinerInfo.class))), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = MinerVo.class))))
    @PostMapping("")
    public String get(@RequestBody ReqMinerInfo req) {
        if (null == req || StringUtils.isBlank(req.getSn()) ||
                StringUtils.isBlank(req.getMi()) || req.getV() == 0)
            return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
        StopWatch w = new StopWatch("watch miner get miner info");
        try {
            // w.start("check machine version");
            // checkMachineVersion(req);
            // w.stop();
            w.start("fetch data");
            MinerVo minerVo = minerService.findBySn(req.getSn());
            w.stop();
            if (null == minerVo)
                return DataResponse.fail(HttpStatusExtend.ERROR_INVALID_REQUEST);
            if (StringUtils.isBlank(minerVo.getMachineId())) { // update machine id in db, this column is unchangeable
                w.start("upgrade miner info of the machine id and cpu");
                minerService.updateMinerMachineIdAndCpu(req.getSn(), req.getMi(),
                        req.getCm());
                minerVo = minerService.findBySn(req.getSn());
                w.stop();
            }
            if (StringUtils.isNotBlank(req.getVer()) && !StringUtils.equals(req.getVer(),
                    minerVo.getVer())) {
                w.start("upgrade miner info of the version");
                minerVo.setV(req.getV()).setVer(req.getVer());
                minerMapper.updateVer(minerVo.getSn(), minerVo.getV(), minerVo.getVer());
                minerService.cleanCache(minerVo.getSn(), minerVo.getWalletAddress());
                w.stop();
            }
            if (StringUtils.isNotBlank(req.getDv()) && !StringUtils.equals(req.getDv(),
                    minerVo.getDv())) {
                w.start("upgrade miner info of the device version");
                minerVo.setDv(req.getDv());
                minerMapper.updateDv(minerVo.getSn(), minerVo.getDv());
                minerService.cleanCache(minerVo.getSn(), minerVo.getWalletAddress());
                w.stop();
            }
            // hashService.queryMiner(minerVo);
            return DataResponse.success(minerVo);
        } catch (Exception e) {
            if (e instanceof AppBizException) {
                log.warn(e.getMessage());
                return DataResponse.fail((AppBizException) e);
            }
            log.error(e.getMessage(), e);
            return DataResponse.fail(e);
        } finally {
            if (w.isRunning())
                w.stop();
            // log.info("watch miner get miner info: {}", w.prettyPrint());
        }
    }

    @Operation(method = "GET", description = "query the reg time of the miner", parameters = @Parameter(name = "sn", description = "machine serial code", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))))
    @GetMapping("check/reg/{sn}")
    public String checkReg(@PathVariable("sn") String sn) {
        MinerVo vo = minerService.findBySn(sn);
        if (null == vo)
            return DataResponse.fail(HttpStatusExtend.ERROR_MINER_SN_CODE);
        return DataResponse.success(DateUtil.format(vo.getLeaveFactory(), "yyyy-MM-dd HH:mm:ss"));
    }

    // /**
    // * validate machine version
    // *
    // * @param req
    // */
    // private void checkMachineVersion(ReqMinerInfo req) {
    // JSONObject machineRule = ruleService.get(Rule.Type.MinerMachine);
    // JSONArray vers = machineRule.getJSONArray("vers");
    // boolean verMatch = false;
    // for (int i = 0; i < vers.size(); i++) {
    // verMatch = vers.getInteger(i).intValue() == req.getV();
    // if (verMatch) break;
    // }
    // if (!verMatch)
    // throw new
    // AppBizException(HttpStatusExtend.ERROR_MINING_MINER_VERSION_EXPIRED,
    // req.getSn() + "=" + req.getV());
    // }

    @Operation(method = "POST", description = "machine bind wallet address", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "bind wallet address param", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Miner.class))), responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))))
    @PostMapping("/wallet")
    public String bindWallet(@RequestBody @Validated Miner miner) {
        log.debug("machine bind wallet address");
        String sn = miner.getSn();
        String a = miner.getWalletAddress();
        Wallet w = walletService.findByAddress(a);
        if (StringUtils.isBlank(sn)) {
            throw new AppBizException(HttpStatusExtend.ERROR_COMMON_DEV_PARAM_IS_NONE);
        } else if (StringUtils.isBlank(a)) {
            throw new AppBizException(HttpStatusExtend.ERROR_COMMON_DEV_PARAM_IS_NONE);
        }
        Miner m = minerService.findBySn(sn);
        if (!StringUtils.isBlank(m.getWalletAddress())) {// If there is a wallet address, you need to unlock the old
                                                         // wallet address to unbind first
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_HAS_BIND_WALLET);
        } else if (!StringUtils.isBlank(m.getPromotionAddress())) {// If the wallet address is empty, there is a mining
                                                                   // pool address, I hope the binding wallet address
                                                                   // needs to be under the old mining pool address
            // Compare whether the address of the two is consistent, if it is not
            // consistent, suspend this operation
            if (StringUtils.isBlank(w.getPromotionAddress())) {// Wallets are not selected from the mining pool
                throw new AppBizException(HttpStatusExtend.ERROR_MINER_BIND_WALLET_PROMOTION_ADDRESS_IS_BLANK);
            } else if (!((m.getPromotionAddress()).equals(w.getPromotionAddress()))) {// Wallet ore pond address is
                                                                                      // inconsistent with the mining
                                                                                      // ponds in the mining machine
                throw new AppBizException(HttpStatusExtend.ERROR_MINER_BIND_WALLET_PROMOTION_ADDRESS_NOT_MATCH);
            }
        }
        minerService.updateMinerWallet(sn, a, w.getPromotionAddress());
        return DataResponse.success();
    }

    @Operation(method = "DELETE", description = "machine unbind wallet address", parameters = {
            @Parameter(name = "sn", description = "machine serial code", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true),
            @Parameter(name = "wa", description = "wallet address", in = ParameterIn.PATH, schema = @Schema(implementation = String.class), required = true)
    }, responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))))
    @DeleteMapping("/wallet/{sn}/{wa}")
    public String unbindWallet(@PathVariable("sn") String sn, @PathVariable("wa") String wa) {
        log.debug("machine unbind wallet address");
        if (StringUtils.isBlank(sn)) {
            throw new AppBizException(HttpStatusExtend.ERROR_COMMON_DEV_PARAM_IS_NONE);
        } else if (StringUtils.isBlank(wa)) {
            throw new AppBizException(HttpStatusExtend.ERROR_COMMON_DEV_PARAM_IS_NONE);
        }
        MinerVo m = minerService.findBySn(sn);
        if (StringUtils.isBlank(m.getWalletAddress())) {
            log.warn("Error unbinding wallet: Wallet address is empty");
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_UNBIND_WALLET);
        } else if (!StringUtils.equals(m.getWalletAddress(), wa)) {
            log.warn("Error unbinding wallet: Wrong wallet address");
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_UNBIND_WALLET);
        } else if (!m.isCanPledger()) {// The mining machine is in a state of pledge, indicating that the unpuffed or
                                       // the redemption is completed
            log.warn("Error unbinding wallet: The mining machine is not in a pledgedstate");
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_UNBIND_WALLET);
            // } else if (!m.isCanTax() && !m.isHasTaxed()) {
            // log.warn("Error unbinding wallet: The mining tax process is not over");
            // throw new AppBizException(HttpStatusExtend.ERROR_MINER_UNBIND_WALLET);
        }
        minerService.updateMinerWallet(sn, "", "");
        return DataResponse.success();
    }

}
