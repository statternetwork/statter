package com.statter.statter.manager.api.controller.v1;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.statter.statter.base.entity.Promotion;
import com.statter.statter.base.mapper.PromotionMapper;
import com.statter.statter.common.service.service.HashService;
import com.statter.statter.common.service.service.MinerService;
import com.statter.statter.common.service.service.PromotionService;
import com.statter.statter.common.service.vo.info.MinerVo;
import com.statter.statter.constant.restful.DataResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "manager: hash")
@RequestMapping("statter/manager/api/v1/hash")
@RestController("openHashController")
public class HashController {

    @Autowired
    MinerService minerService;

    @Autowired
    HashService hashService;

    @Autowired
    PromotionMapper promotionMapper;

@PutMapping("/")
public String update(@RequestBody String j) {
log.debug("upload machine hash info：{}", j);
JSONObject p = JSONObject.parseObject(j);
String sn = p.getString("sn");
MinerVo m = minerService.findBySn(sn);
long max = p.getLongValue("m");
if (max > m.getMaxHistoryHash()) {// refresh machine max hash in db when req is greater than db
hashService.updateMaxHash(sn, max);
}
hashService.update(sn, p.getLongValue("a"));// Real-time broadcast
return DataResponse.success();
}

    @GetMapping("/total")
    public String getTotal() {
        return DataResponse.success(hashService.getTotal());
    }

    @GetMapping("/global")
    public String getGlobal() {
        JSONObject global = new JSONObject();
        global.put("g", hashService.getTotal());
        JSONArray arr = new JSONArray();
        List<Promotion> promotionList = promotionMapper.findAllValid();
        Map<String, String> promotionHashs = hashService.getAllPromotion();
        promotionList.forEach(p -> {
            String hs = promotionHashs.get(p.getAddress());
            long h = StringUtils.isBlank(hs) ? 0 : Long.parseLong(hs);
            if (h < 1)
                return;
            JSONObject t = new JSONObject();
            t.put("c", p.getCode());
            // t.put("a",p.getAlias());
            t.put("h", h);
            arr.add(t);
        });
        global.put("ps", arr);
        return DataResponse.success(global);
    }

}
