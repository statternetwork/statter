package com.statter.statter.administrator.api.controller.v1.version;

import com.statter.statter.administrator.api.controller.v1.version.vo.ReqMinerVersionAddNewVer;
import com.statter.statter.base.entity.Rule;
import com.statter.statter.common.service.service.RuleService;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.constant.restful.DataResponse;
import com.statter.statter.util.JSONUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Api(value = "version publish manage")
@RequestMapping("v1/version")
@RestController("adminVersionController")
public class VersionController {

    @Autowired
    RuleService ruleService;

    @ApiOperation(httpMethod = "POST", value = "publish new miner program")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @PostMapping("/miner/ver")
    public String addNewMinerVer(@ApiParam(value = "new miner program info", type = "json", required = true)
                                     @RequestBody ReqMinerVersionAddNewVer req) {
        if (null == req || !req.validate()) {
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        }
        ruleService.add(Rule.Type.MinerMachine.getValue(), JSONUtils.toJSONObject(req));
        return DataResponse.success();
    }

}
