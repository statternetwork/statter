// package com.synctech.statter.task.task;

// import cn.hutool.core.util.NumberUtil;
// import cn.hutool.json.JSONArray;
// import com.alibaba.fastjson.JSONObject;
// import com.synctech.statter.base.entity.Ledger;
// import com.synctech.statter.base.entity.Promotion;
// import com.synctech.statter.base.mapper.LedgerMapper;
// import com.synctech.statter.base.mapper.PromotionLedgerMapper;
// import com.synctech.statter.base.mapper.PromotionMapper;
// import com.synctech.statter.common.pool.service.PoolService;
// import com.synctech.statter.common.service.vo.info.MinerVo;
// import com.synctech.statter.constant.CacheKey;
// import com.synctech.statter.redis.jedis.JedisService;
// import lombok.extern.slf4j.Slf4j;
// import net.javacrumbs.shedlock.core.SchedulerLock;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.util.CollectionUtils;
// import redis.clients.jedis.Jedis;

// import org.springframework.beans.factory.annotation.Autowired;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// @Component
// @Slf4j
// public class MiningJobs {

//     @Autowired
//     JedisService jedisService;
//     @Autowired
//     PoolService poolService;
//     @Autowired
//     LedgerMapper ledgerMapper;
//     @Autowired
//     PromotionMapper promotionMapper;

//     @Autowired
//     PromotionLedgerMapper promotionLedgerMapper;

//     @Value("${statter.promotion.ledger.cache.size:1000}")
//     private Integer promotionLedgerCacheSize;

//     /**
//      * Timing task:
//      * - Get and refresh the block index
//      * - check the mineral mining task
//      * - clean up the task of past block height
//      */
//     @Scheduled(fixedDelay = 1000)
//     @SchedulerLock(name = "MiningJobs.clearBlockIndexQuestion", lockAtLeastFor = 100, lockAtMostFor = 30000)
//     public void clearBlockIndexQuestion() {
//         log.info("Timing task: : Start to clear expire block index ...");
//         long cbi = poolService.getBlockIndexImpl();
//         Jedis j = jedisService.get();
//         try {
//             j.set(CacheKey.CACHEKEY_MINING_BLOCK_INDEX, cbi + "");// refresh block index
//             // find past question
//             Set<String> keys = j.keys(CacheKey.CACHEKEY_MINING_QUESTION_KEY_PREFIX + "*");
//             if (CollectionUtils.isEmpty(keys)) {
//                 return;
//             }
//             for (String k : keys) {
//                 if (!StringUtils.startsWithIgnoreCase(k, CacheKey.CACHEKEY_MINING_QUESTION_KEY_PREFIX)) {
//                     continue;
//                 }
//                 long i = NumberUtil.parseLong(k.substring(CacheKey.CACHEKEY_MINING_QUESTION_KEY_PREFIX.length()));
//                 if (i != cbi) {// Clean up
//                     long r = j.del(k);
//                 }
//             }
//         } finally {
//             jedisService.flush(j);
//         }
//     }

//     @Scheduled(fixedDelay = 1000)
//     @SchedulerLock(name = "MiningJobs.clearLedger", lockAtLeastFor = 100, lockAtMostFor = 30000)
//     public void clearLedger() {
//         log.info("Timing task: : Start to clear expire ledger ...");
//         String v = jedisService.get(CacheKey.CACHEKEY_MINING_BLOCK_INDEX);
//         if (StringUtils.isBlank(v)) {
//             return;
//         }
//         long curBlockIndex = NumberUtil.parseLong(v);
//         Jedis j = jedisService.get();
//         try {
//             Set<String> keys = j.keys(CacheKey.CACHEKEY_MINING_LEDGER_KEY_PREFIX + "*");
//             if (CollectionUtils.isEmpty(keys)) {
//                 return;
//             }
//             for (String k : keys) {
//                 if (!StringUtils.startsWithIgnoreCase(k, CacheKey.CACHEKEY_MINING_LEDGER_KEY_PREFIX)) {
//                     continue;
//                 }
//                 long i = NumberUtil.parseLong(k.substring(CacheKey.CACHEKEY_MINING_LEDGER_KEY_PREFIX.length()));
//                 if (i != curBlockIndex) {
//                     long r = j.del(k);
//                 }
//             }
//         } finally {
//             jedisService.flush(j);
//         }
//     }

//     @Transactional
//     @Scheduled(fixedDelay = 5000)
//     @SchedulerLock(name = "MiningJobs.processLedger", lockAtLeastFor = 100, lockAtMostFor = 30000)
//     public void processLedger() {
//         log.info("Timing task: : Start to process valid ledger ...");
//         List<Ledger> unprocessedLedgers = ledgerMapper.findValid();
//         if (CollectionUtils.isEmpty(unprocessedLedgers))
//             return;
//         a: for (Ledger l : unprocessedLedgers) {
//             JSONObject newLedgerData = new JSONObject();
//             JSONObject d = JSONObject.parseObject(l.getData());
//             for (Map.Entry<String, Object> entry : d.entrySet()) {
//                 MinerVo m = jedisService.hget(CacheKey.CACHEKEY_INFO_MINER_BY_SN, entry.getKey(), MinerVo.class);
//                 if (null == m)
//                     break a;
//                 if (StringUtils.equals(l.getPromotionAddress(), m.getPromotionAddress())) { // reserve
//                     newLedgerData.put(entry.getKey(), entry.getValue());
//                 }
//             }
//             String r = newLedgerData.toJSONString();
//             l.setData(r.getBytes());

//             // move the valuable data to the promotion table
//             int c = promotionLedgerMapper.existTable(l.getPromotionAddress());
//             if (c == 0) {
//                 log.info("the ledger table is not exist, ready to create ledger table: ledger_"
//                         + l.getPromotionAddress());
//                 promotionLedgerMapper.createNewTable(l.getPromotionAddress());// create promotion ledger table
//             }
//             promotionLedgerMapper.insert(l.getPromotionAddress(), l);
//             ledgerMapper.delete(l.getId());

//             log.debug("Timing task [process ledger]: the data of ledger[{}] has processed, the content is -- {}",
//                     l.getId(), new String(l.getData()));
//         }
//     }

//     /**
//      * cache promotion ledger every minute
//      */
//     @Scheduled(fixedDelay = 60000)
//     @SchedulerLock(name = "MiningJobs.cachePromotionLedger", lockAtLeastFor = 100, lockAtMostFor = 300000)
//     public void cachePromotionLedger() {
//         log.info("Timing task [cache promotion ledger]: start");
//         List<Promotion> promotions = promotionMapper.findAll();
//         if (CollectionUtils.isEmpty(promotions))
//             return;
//         for (Promotion p : promotions) {
//             try {
//                 if (1 != promotionLedgerMapper.existTable(p.getAddress()))
//                     continue;// exist no table
//                 List<Ledger> list = promotionLedgerMapper.findLimit(p.getAddress(), promotionLedgerCacheSize);
//                 JSONArray arr = new JSONArray();
//                 if (!CollectionUtils.isEmpty(list))
//                     arr.addAll(list);
//                 jedisService.hset(CacheKey.CACHEKEY_LEDGER_LIST_BY_PROMOTION, p.getAddress(), arr.toString());
//             } catch (Exception e) {
//             }
//         }
//         log.info("Timing task [cache promotion ledger]: end");
//     }

// }
