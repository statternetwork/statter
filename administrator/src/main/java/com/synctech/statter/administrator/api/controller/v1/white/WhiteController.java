package com.synctech.statter.administrator.api.controller.v1.white;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.base.entity.White;
import com.synctech.statter.common.service.service.RuleService;
import com.synctech.statter.common.service.service.WhiteService;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.redis.jedis.JedisService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Api(value = "rule manage")
@RequestMapping("v1/white")
@RestController("adminWhiteController")
public class WhiteController {

    @Autowired
    JedisService jedisService;

    @Autowired
    RuleService ruleService;

    @Autowired
    WhiteService whiteService;

    @ApiOperation(httpMethod = "PUT", value = "open white list module")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @PutMapping("/open")
    public String open() {
        if (whiteService.isWhiteModelOpen()) {
            return DataResponse.success();
        }
        JSONObject j = new JSONObject();
        j.put("minerWhiteListSwitch", Boolean.TRUE);
        ruleService.add(Rule.Type.WhiteList.getValue(), j);
        return DataResponse.success();
    }

    @ApiOperation(httpMethod = "PUT", value = "close white list module")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @PutMapping("/close")
    public String close() {
        if (!whiteService.isWhiteModelOpen()) {
            return DataResponse.success();
        }
        JSONObject j = new JSONObject();
        j.put("minerWhiteListSwitch", Boolean.FALSE);
        ruleService.add(Rule.Type.WhiteList.getValue(), j);
        return DataResponse.success();
    }


    @ApiOperation(httpMethod = "POST", value = "add rule")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @PostMapping("/miner/add/{sn}")
    public String addMiner(
            @ApiParam(name = "sn", value = "miner sn", type = "String", required = true) @PathVariable("sn") String sn) {
        whiteService.add(sn, White.Type.Miner);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, sn);
        return DataResponse.success();
    }

}
