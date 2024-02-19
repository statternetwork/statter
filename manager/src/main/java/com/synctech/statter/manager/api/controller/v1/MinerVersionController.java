package com.synctech.statter.manager.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.common.service.service.RuleService;
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
@Tag(name = "manager: miner")
@RequestMapping("statter/manager/api/v1/miner/update")
@RestController("openMinerVersionController")
public class MinerVersionController {

    @Autowired
    RuleService ruleService;

    @Operation(method = "GET", description = "version check script download url", responses = @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))))
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
