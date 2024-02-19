package com.synctech.statter.common.service.service;

import cn.hutool.core.date.DateUtil;
import com.synctech.statter.base.entity.Process;
import com.synctech.statter.base.mapper.ProcessMapper;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.common.service.vo.info.WalletVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProcessService {

//    @Value("${statter.mining.pledge.wait-to-next-day}")
//    boolean miningPledgeWaitToNextDay;

    @Autowired
    ProcessMapper processMapper;

    @Autowired
    WhiteService whiteService;

    public List<Process> findAll() {
        return processMapper.findAll();
    }

    /**
     * query process info (replenish miner info)
     *
     * @param m
     */
    public void query(MinerVo m) {
        boolean isWhite = whiteService.isWhiteMiner(m.getSn());
        query(m, isWhite);
    }

    public void query(MinerVo m, boolean isWhite) {
        if (isWhite) {
            m.setPledgeDate("-");
            m.setCanPledger(false);
            m.setCanRedemption(false);
            m.setCanMining(true);
        } else {
            if (m.getPledgeProcessId() == 0) {// no pledge nor redemption data
                m.setPledgeDate("-");
                m.setCanPledger(true);
                m.setCanRedemption(false);
            } else {
                Process p = processMapper.findOne(m.getPledgeProcessId());
                m.processPledgeImpl(p);
                m.processMiningImpl();
            }
        }
    }

    public void query(MinerVo m, boolean isWhite, Process p) {
        if (isWhite) {
            m.setPledgeDate("-");
            m.setCanPledger(false);
            m.setCanRedemption(false);
            m.setCanMining(true);
        } else {
            if (m.getPledgeProcessId() == 0) {// no pledge nor redemption data
                m.setPledgeDate("-");
                m.setCanPledger(true);
                m.setCanRedemption(false);
            } else if (null != p) {
                m.processPledgeImpl(p);
                m.processMiningImpl();
            }
        }
    }

//    /**
//     * process:pledge
//     *
//     * @param m
//     */
//    private void processPledge(MinerVo m) {
//        if (whiteService.isWhiteMiner(m.getSn())) {
//            m.setPledgeDate("-");
//            m.setCanPledger(false);
//            m.setCanRedemption(false);
//        } else if (m.getPledgeProcessId() == 0) {// no pledge nor redemption data
//            m.setPledgeDate("-");
//            m.setCanPledger(true);
//            m.setCanRedemption(false);
//        } else {
//            Process p = processMapper.findOne(m.getPledgeProcessId());
//            m.processPledgeImpl(p);
//        }
//    }
//
//    /**
//     * process:check the miner can or not do mining
//     *
//     * @param m
//     */
//    private void processMining(MinerVo m) {
//        if (whiteService.isWhiteMiner(m.getSn())) {
//            m.setCanMining(true);
//        } else {
//            m.processMiningImpl(miningPledgeWaitToNextDay);
//        }
//    }

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
