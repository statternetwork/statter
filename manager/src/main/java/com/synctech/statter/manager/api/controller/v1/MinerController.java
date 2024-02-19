package com.synctech.statter.manager.api.controller.v1;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Miner;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.common.service.service.HashService;
import com.synctech.statter.common.service.service.MinerService;
import com.synctech.statter.common.service.service.RuleService;
import com.synctech.statter.common.service.service.WalletService;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.manager.api.controller.v1.vo.ReqMinerInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Api(value = "machine manage")
@RequestMapping("v1/miner")
@RestController("openMinerController")
public class MinerController {

    @Autowired
    MinerService minerService;

    @Autowired
    WalletService walletService;

    @Autowired
    RuleService ruleService;

    @Autowired
    HashService hashService;

    @ApiOperation(httpMethod = "POST", value = "query machine info")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = MinerVo.class) })
    @PostMapping("")
    public String get(
            @ApiParam(value = "basic machine param", type = "json", required = true) @RequestBody ReqMinerInfo req) {
        if (null == req || StringUtils.isBlank(req.getSn()) || StringUtils.isBlank(req.getMi()) || req.getV() == 0)
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        checkMachineVersion(req.getV());
        MinerVo minerVo = minerService.findBySn(req.getSn());
        if (StringUtils.isBlank(minerVo.getMachineId())) { // update machine id in db, this column is unchageable
            minerService.updateMinerMachineIdAndCpu(req.getSn(), req.getMi(), req.getCm());
            minerVo = minerService.findBySn(req.getSn());
        } else if (!StringUtils.equals(minerVo.getMachineId(), req.getMi())) {// Illegal visit, multi use with one sn
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        hashService.queryMiner(minerVo);
        return DataResponse.success(minerVo);
    }

    /**
     * validate machine version
     *
     * @param ver
     */
    private void checkMachineVersion(int ver) {
        JSONObject machineRule = ruleService.get(Rule.Type.MinerMachine);
        JSONArray vers = machineRule.getJSONArray("vers");
        boolean verMatch = false;
        for (int i = 0; i < vers.size(); i++) {
            verMatch = vers.getInteger(i).intValue() == ver;
            if (verMatch)
                break;
        }
        if (!verMatch)
            throw new AppBizException(HttpStatusExtend.ERROR_MINING_MINER_VERSION_EXPIRED);
    }

    @ApiOperation(httpMethod = "POST", value = "machine bind wallet address")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
    @PostMapping("/wallet")
    public String bindWallet(
            @ApiParam(value = "bind wallet address param", type = "json", required = true) @RequestBody @Validated Miner miner) {
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
        minerService.updateMinerWallet(sn, a);
        return DataResponse.success();
    }

    @ApiOperation(httpMethod = "DELETE", value = "machine unbind wallet address")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
    @DeleteMapping("/wallet/{sn}/{wa}")
    public String unbindWallet(
            @ApiParam(name = "sn", value = "machine sn code", type = "String", required = true) @PathVariable("sn") String sn,
            @ApiParam(name = "wa", value = "wallet address", type = "String", required = true) @PathVariable("wa") String wa) {
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
            log.warn("Error unbinding wallet: The mining machine is not in a pledged state");
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_UNBIND_WALLET);
            // } else if (!m.isCanTax() && !m.isHasTaxed()) {
            // log.warn("Error unbinding wallet: The mining tax process is not over");
            // throw new AppBizException(HttpStatusExtend.ERROR_MINER_UNBIND_WALLET);
        }
        minerService.updateMinerWallet(sn, "");
        return DataResponse.success();
    }

}
