package com.synctech.statter.administrator.api.controller.v1.version;

import com.synctech.statter.administrator.api.controller.v1.version.vo.ReqMinerVersionAddNewVer;
import com.synctech.statter.base.entity.Rule;
import com.synctech.statter.common.service.service.RuleService;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.util.JSONUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@Api(value = "version publish manage")
@RequestMapping("v1/version")
@RestController("adminVersionController")
public class VersionController {

    @Resource
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
