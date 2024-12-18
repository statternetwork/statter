package com.statter.statter.task.task;

import com.statter.statter.base.entity.TradeFlow;
import com.statter.statter.base.mapper.BlockCrawlerMapper;
import com.statter.statter.base.mapper.LedgerMapper;
import com.statter.statter.base.mapper.TradeFlowMapper;
import com.statter.statter.common.pool.service.PoolService;
import com.statter.statter.common.pool.vo.Block;
import com.statter.statter.task.service.BlockCrawlerService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component
@Slf4j
public class BlockCrawlerJobs {

    @Autowired
    LedgerMapper ledgerMapper;

    @Autowired
    BlockCrawlerMapper blockCrawlerMapper;

    @Autowired
    TradeFlowMapper tradeFlowMapper;

    @Autowired
    BlockCrawlerService blockCrawlerService;

    @Autowired
    PoolService poolService;

    /**
     * download block data, and analyze into the database
     */
    @Transactional
    @Scheduled(fixedDelay = 1000)
    @SchedulerLock(name = "BlockCrawlerJobs.crawlerBlock", lockAtLeastFor = 100, lockAtMostFor = 60000)
    public void crawlerBlock() {
        log.info("Timing task: : Start to crawler block data ...");
        // Query the highest block number that has been pale
        Long bbi = blockCrawlerMapper.max();
        long nbi = null == bbi ? 1 : (bbi + 1);
        long cbi = poolService.getBlockIndexImpl();
        if (nbi >= cbi) {// If the 'nbi' is greater than equal to the current height of the mining pool
                         // block, then it ends
            return;
        }
        try {
            // 1. confirm block ledger
            Block block = poolService.queryBlockLedgerId(nbi);
            if (null != block) {
                ledgerMapper.tagValid(block.getPid(), block.getMingProfit());// tag the seperate and record profit
                ledgerMapper.deleteInvalid(nbi);// delete others on this block index
            }
            // 2. catch trade flow
            List<TradeFlow> tradeList = blockCrawlerService.getTradeList(nbi);
            if (!CollectionUtils.isEmpty(tradeList)) {
                for (TradeFlow tf : tradeList) {
                    tf.setBlockIndex(nbi);
                    tf.setStage((byte) 0);
                    tradeFlowMapper.add(tf);
                }
            }
        } finally {
            blockCrawlerMapper.add(nbi);
            blockCrawlerMapper.deleteBlow(nbi);
        }
    }

}
