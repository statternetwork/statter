package com.synctech.statter.manager.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.common.service.service.RuleService;
import com.synctech.statter.constant.restful.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "manager: rule")
@RequestMapping("statter/manager/api/v1/rule")
@RestController("openRuleController")
public class RuleController {

    @Autowired
    RuleService ruleService;

    @Operation(method = "GET", description = "tax and pledge rules of miners", responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))))
    @GetMapping("/miner/tap")
    public String minerTaxAndPledge() {
        JSONObject minerManageRule = new JSONObject();
        JSONObject taxRule = ruleService.get(Rule.Type.MinerTax);
        JSONObject pledgeRule = ruleService.get(Rule.Type.MinerPledge);
        minerManageRule.put("minerTax", taxRule);
        minerManageRule.put("minerPledge", pledgeRule);
        return DataResponse.success(minerManageRule);
    }

    @Operation(method = "GET", description = "tax or pledge rules of wallets", responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))))
    @GetMapping("/wallet/p")
    public String walletPledge() {
        JSONObject minerManageRule = new JSONObject();
        JSONObject walletRule = ruleService.get(Rule.Type.WalletPledge);
        minerManageRule.put("walletPledge", walletRule);
        return DataResponse.success(minerManageRule);
    }

}
