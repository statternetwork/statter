package com.statter.statter.task.task;

import com.statter.statter.base.entity.TradeFlow;
import com.statter.statter.base.mapper.TradeFlowMapper;
import com.statter.statter.task.service.TradeFlowService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component
@Slf4j
public class TradeFlowJobs {

    @Autowired
    TradeFlowMapper tradeFlowMapper;

    @Autowired
    TradeFlowService tradeFlowService;

    /**
     * Analyze the transaction flow, and conduct business processing for the trading
     * that belongs to the mining tax and pledge
     */
    @Scheduled(fixedDelay = 1000)
    @SchedulerLock(name = "TradeFlowJobs.analyzeTradeFlow", lockAtLeastFor = 100, lockAtMostFor = 60000)
    public void analyzeTradeFlow() {
        log.info("Timing task: : Start to analyze trade flows ...");
        // According to the height of the block, find the unprepared data
        List<TradeFlow> l = tradeFlowMapper.findLowestBlockIndexWithNotProcess();
        if (CollectionUtils.isEmpty(l)) {
            return;
        }
        log.info(
                "Analyze the transaction flow: Discovered the unpreposed transaction flow, and started business running water treatment");
        for (TradeFlow f : l) {
            log.info("Analyze the transaction flow -- start: {}", f);
            if (TradeFlow.TradeType.Tax.compare(f.getTradeType())) {
                if ("1".equals(f.getTradeSubType())) {// wallet tax, to apply for promotion
                    log.info("Analyze the transaction flow -- process wallet tax/pledge");
                    tradeFlowService.walletPledge(f);
                }
            } else if (TradeFlow.TradeType.Pledge.compare(f.getTradeType())) {
                if (TradeFlow.TradeSubType.MinerPledge.compare(f.getTradeSubType())) {
                    log.info("Analyze the transaction flow -- process miner pledge");
                    tradeFlowService.minerPledge(f);
                } else if (TradeFlow.TradeSubType.MinerRedempt.compare(f.getTradeSubType())) {
                    log.info("Analyze the transaction flow -- process miner redemption");
                    tradeFlowService.minerRedempt(f);
                } else {// unknown
                    tradeFlowService.unknowFlow(f);
                }
            } else {// Other transaction data, directly transfer to the unknown flow meter
                tradeFlowService.unknowFlow(f);
            }
        }

    }

}
