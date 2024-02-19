package com.synctech.statter.administrator.api.controller.v1.tax;

import com.synctech.statter.administrator.api.controller.v1.tax.vo.ReqTaxRule;
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

import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Api(value = "tax manage")
@RequestMapping("v1/tax")
@RestController("adminTaxController")
public class TaxController {

    @Autowired
    RuleService ruleService;

    @ApiOperation(httpMethod = "POST", value = "add rule")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @PostMapping("/rule")
    public String add(@ApiParam(value = "rule json content", type = "json", required = true) @RequestBody ReqTaxRule r) {
        if (null == r || !r.validate())
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        ruleService.add(Rule.Type.MinerTax.getValue(), JSONUtils.toJSONObject(r));
        return DataResponse.success();
    }

}
