package com.synctech.statter.manager.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.common.service.service.RuleService;
import com.synctech.statter.constant.restful.DataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@Api(value = "api about biz rule")
@RequestMapping("v1/rule")
@RestController("openRuleController")
public class RuleController {

    @Resource
    RuleService ruleService;

    @ApiOperation(httpMethod = "GET", value = "tax and pledge rules of miners ")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GetMapping("/miner/tap")
    public String minerTaxAndPledge() {
        JSONObject minerManageRule = new JSONObject();
        JSONObject taxRule = ruleService.get(Rule.Type.MinerTax);
        JSONObject pledgeRule = ruleService.get(Rule.Type.MinerPledge);
        minerManageRule.put("minerTax", taxRule);
        minerManageRule.put("minerPledge", pledgeRule);
        return DataResponse.success(minerManageRule);
    }

    @ApiOperation(httpMethod = "GET", value = "tax or pledge rules of wallets")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GetMapping("/wallet/p")
    public String walletPledge() {
        JSONObject minerManageRule = new JSONObject();
        JSONObject walletRule = ruleService.get(Rule.Type.WalletPledge);
        minerManageRule.put("walletPledge", walletRule);
        return DataResponse.success(minerManageRule);
    }

}
