package com.synctech.statter.manager.api.controller.v1;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.common.service.service.HashService;
import com.synctech.statter.common.service.service.MinerService;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.restful.DataResponse;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(value = "hash manage")
@RequestMapping("v1/hash")
@RestController("openHashController")
public class HashController {

    @Autowired
    MinerService minerService;

    @Autowired
    HashService hashService;

    @ApiOperation(httpMethod = "PUT", value = "upload machine hash info")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @PutMapping("/")
    public String update(@ApiParam(value = "hash info", type = "json", example = "{'sn':'xxxxx','a':111,'m':111}", required = true) @RequestBody String j) {
        log.debug("upload machine hash info：{}", j);
        JSONObject p = JSONObject.parseObject(j);
        String sn = p.getString("sn");
        MinerVo m = minerService.findBySn(sn);
        long max = p.getLongValue("m");
        if (max > m.getMaxHistoryHash()) {// refresh machine max hash in db when req is greater than db
            hashService.updateMaxHash(sn, max);
        }
        hashService.update(sn, p.getLongValue("a"));
        return DataResponse.success();
    }

    @ApiOperation(httpMethod = "GET", value = "Obtain total hash of all promotion")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = String.class)})
    @GetMapping("/total")
    public String getTotal() {
        return DataResponse.success(hashService.getTotal());
    }

}
