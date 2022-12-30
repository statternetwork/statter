package com.synctech.statter.administrator.api.controller.v1.pool;

import com.synctech.statter.base.entity.ApiLimit;
import com.synctech.statter.base.mapper.ApiLimitMapper;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.redis.jedis.JedisService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@Api(value = "api limit manage")
@RequestMapping("v1/mining/pool/api")
@RestController("adminMiningPoolApiController")
public class MiningPoolApiController {

    @Resource
    JedisService jedisService;

    @Resource
    ApiLimitMapper apiLimitMapper;

    @ApiOperation(httpMethod = "POST", value = "add limit rule")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @PostMapping("")
    public String add(@ApiParam(value = "limit json content", type = "json", required = true) @RequestBody ApiLimit r) {
        if (null == r || !r.validate())
            throw new AppBizException(HttpStatusExtend.ERROR_INVALID_REQUEST);
        // TODO: delay delay delay delay delay delay delay delay delay delay delay delay delay delay delay delay delay
        return DataResponse.success();
    }

}
