package com.synctech.statter.common.service.service;

import cn.hutool.core.date.DateUtil;
import com.synctech.statter.base.entity.Process;
import com.synctech.statter.base.mapper.ProcessMapper;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.common.service.vo.info.WalletVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class ProcessService {

    @Value("${statter.mining.pledge.wait-to-next-day}")
    boolean miningPledgeWaitToNextDay;

    @Resource
    ProcessMapper processMapper;

    @Resource
    WhiteService whiteService;

    /**
     * query process info (replenish miner info)
     *
     * @param m
     */
    public void query(MinerVo m) {
        this.processPledge(m);
        this.processMining(m);
    }

    /**
     * process:check the miner can or not do mining
     *
     * @param m
     */
    private void processMining(MinerVo m) {
        if (whiteService.isWhiteMiner(m.getSn())) {
            m.setCanMining(true);
            return;
        }
        if (!m.isHasPledged()) {
            m.setCanMining(false);
            return;
        }
        long nds = System.currentTimeMillis() / 86400000; // the days is away from 1970-01-01
        long pds = DateUtil.parse(m.getPledgeDate(), "yyyy-MM-dd HH:mm:ss").getTime() / 86400000;
        if (nds > pds) {
            m.setCanMining(true);
            log.info("miner info processMining: the miner has pledged, it can do mining");
        } else if (!miningPledgeWaitToNextDay) {
            m.setCanMining(true);
            log.info("miner info processMining: the miner has pledged, and not need to wait to next day, so it can do mining");
        } else {
            m.setCanMining(false);
            log.info("miner info processMining: the miner has pledged, but need to wait to next day");
        }
    }

    /**
     * process:pledge
     *
     * @param m
     */
    private void processPledge(MinerVo m) {
        if (whiteService.isWhiteMiner(m.getSn())) {
            m.setPledgeDate("-");
            m.setCanPledger(false);
            m.setCanRedemption(false);
            return;
        } else if (m.getPledgeProcessId() == 0) {// no pledge nor redemption data
            m.setPledgeDate("-");
            m.setCanPledger(true);
            m.setCanRedemption(false);
            return;
        }
        Process p = processMapper.findOne(m.getPledgeProcessId());
        m.setPledgerStage(p.getStage());
        m.setPledgeDate(DateUtil.format(p.getCreatedTime(), "yyyy-MM-dd HH:mm:ss"));
        m.setPledgeAmount(p.getAmount());
        if (Process.Stage.PledgeApply.compare(p.getStage())) {
            m.setCanPledger(false);
            m.setCanRedemption(false);
        } else if (Process.Stage.PledgeHasPayed.compare(p.getStage())) {
            m.setCanPledger(false);
            m.setCanRedemption(false);
        } else if (Process.Stage.PledgeComplete.compare(p.getStage())) {
            m.setCanPledger(false);
            m.setCanRedemption(true);
        } else if (Process.Stage.RedemptionApply.compare(p.getStage())) {
            m.setCanPledger(false);
            m.setCanRedemption(false);
        } else if (Process.Stage.RedemptionComplete.compare(p.getStage())) {
            m.setCanPledger(true);
            m.setCanRedemption(false);
        }
    }

    /**
     * process: wallet pledge
     *
     * @param w
     */
    public void processWalletPledge(WalletVo w) {
        if (w.getPledgeProcessId() == 0) {//  no pledge data
            w.setPledgeDate("-");
            w.setCanPledger(true);
            w.setCanRedemption(false);
            return;
        }
        Process p = processMapper.findOne(w.getPledgeProcessId());
        w.setPledgerStage(p.getStage());
        w.setPledgeDate(DateUtil.format(p.getCreatedTime(), "yyyy-MM-dd HH:mm:ss"));
        w.setPledgeAmount(p.getAmount());
        if (Process.Stage.PledgeApply.compare(p.getStage())) {
            w.setCanPledger(false);
            w.setCanRedemption(false);
        } else if (Process.Stage.PledgeHasPayed.compare(p.getStage())) {
            w.setCanPledger(false);
            w.setCanRedemption(false);
        } else if (Process.Stage.PledgeComplete.compare(p.getStage())) {
            w.setCanPledger(false);
            w.setCanRedemption(true);
        } else if (Process.Stage.RedemptionApply.compare(p.getStage())) {
            w.setCanPledger(false);
            w.setCanRedemption(false);
        } else if (Process.Stage.RedemptionComplete.compare(p.getStage())) {
            w.setCanPledger(true);
            w.setCanRedemption(false);
        }
    }

}
