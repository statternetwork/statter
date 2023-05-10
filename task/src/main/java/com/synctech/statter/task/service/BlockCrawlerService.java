package com.synctech.statter.task.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.base.entity.TradeFlow;
import com.synctech.statter.common.pool.service.PoolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Block crawler service
 */
@Slf4j
@Service
public class BlockCrawlerService {

    @Autowired
    PoolService poolService;

    public List<TradeFlow> getTradeList(long blockIndex) {
        JSONArray arr = poolService.downLoadBlockTradeFlow(blockIndex);
        List<TradeFlow> r = new ArrayList<>();
        if (CollectionUtils.isEmpty(arr)) return r;
        for (Object o : arr) {
            JSONObject jo = (JSONObject) o;
            for (String s : jo.keySet()) {
                JSONObject flow = jo.getJSONObject(s);
                String tt = flow.getString("tradeType");
                if (StringUtils.isBlank(tt)) continue;
                r.add(flow.toJavaObject(TradeFlow.class));
            }
        }
        return r;
    }


}
