package com.synctech.statter.mining.pool.api.controller.v1.miner;

import com.synctech.statter.base.entity.Ledger;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.common.service.vo.info.WalletVo;
import com.synctech.statter.constant.restful.DataResponse;
import com.synctech.statter.mining.pool.api.controller.v1.CommonController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(value = "api of the miner")
@RequestMapping("v1/miner")
@RestController()
public class MinerController extends CommonController {



//    @ApiOperation(httpMethod = "GET", value = "")
//    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = MinerVo[].class)})
//    @GetMapping("/list/{page}/{size}")
//    public String page() {
//        return null;
//    }


}
