package com.synctech.statter.manager.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.common.service.service.RuleService;
import com.synctech.statter.common.service.vo.info.WalletVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Api(value = "mining machine version control")
@RequestMapping("v1/miner/update")
@RestController("openMinerVersionController")
public class MinerVersionController {

    @Autowired
    RuleService ruleService;

    @ApiOperation(httpMethod = "GET", value = "version check script download url")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK", response = String.class) })
    @GetMapping("/")
    public String get() {
        // Pointing to script
        // file:`.../nginx/downloads/statter.update.v1.0.1.sh.template`，
        // After each time the script is released, the version and script are updated,
        // and update the return value of this interface,
        // the final version increases, avoid the update of the same version without
        // triggering the update
        JSONObject machineRule = ruleService.get(Rule.Type.MinerMachine);
        return machineRule.getString("updateScriptUrl");
        // return "http://192.168.1.164/downloads/statter.update.v1.0.1.sh";
    }

}
