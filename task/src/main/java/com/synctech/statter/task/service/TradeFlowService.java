package com.statter.statter.task.service;

import com.statter.statter.base.entity.Process;
import com.statter.statter.base.entity.*;
import com.statter.statter.base.mapper.*;
import com.statter.statter.common.service.service.PromotionService;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Slf4j
@Service
public class TradeFlowService {

    @Autowired
    MinerMapper minerMapper;

    @Autowired
    WalletMapper walletMapper;

    @Autowired
    PromotionService promotionService;

    @Autowired
    ProcessMapper processMapper;

    @Autowired
    ApplyForPromotionMapper applyForPromotionMapper;

    @Autowired
    TradeFlowMapper tradeFlowMapper;

    @Autowired
    JedisService jedisService;

    public void successFlow(TradeFlow f) {
        log.info("Analysis of transaction flow -- processing success: {}", f);
        tradeFlowMapper.delete(f.getTradeNo());
        tradeFlowMapper.addArchive(f);
    }

    public void unknowFlow(TradeFlow f) {
        log.error("Analysis of transaction flow -- unknown transaction type：{}", f);
        tradeFlowMapper.delete(f.getTradeNo());
        tradeFlowMapper.addUnknow(f);
    }

    public void errorFlow(TradeFlow f) {
        tradeFlowMapper.updateStage(f.getTradeNo(), TradeFlow.Stage.Failed.getValue());
    }

    private Miner mineCheck(TradeFlow f, boolean checkTax, boolean checkPledge) {
        String sn = f.getUniqueCode();
        if (org.apache.commons.lang3.StringUtils.isBlank(sn)) {
            log.error("Analysis of transaction flow -- error no miner sn: {}", f);
            this.errorFlow(f);
            return null;
        }
        Miner m = minerMapper.findOne(sn);
        if (null == m) {
            log.error("Analysis of transaction flow -- error unregisted miner sn: {}", f);
            this.errorFlow(f);
            return null;
        }
        if (checkTax) {
            if (m.isHasTaxed()) {
                log.error("Analysis of transaction flow -- error repeatedly pays the flow of mining tax: {}", f);
                this.errorFlow(f);
                return null;
            }
        }
        if (checkPledge) {
            if (m.isHasPledged()) {
                log.error("Analysis of transaction flow -- error repeatedly pays the flow of mining pledge: {}", f);
                this.errorFlow(f);
                return null;
            }
        }
        return m;
    }

    /**
     * The business logic of the trading of mining tax
     *
     * @param f
     */
    @Transactional
    public void minerTax(TradeFlow f) {
        Miner m = this.mineCheck(f, true, false);
        if (null == m)
            return;
        Process p = new Process();
        p.setType(Process.Type.MinerTax.getValue())
                .setStage(Process.Stage.TaxComplete.getValue())
                .setAddress(f.getFrom())
                .setSn(m.getSn())
                .setAmount(f.getTradeAmount())
                .setTradeNo(f.getTradeNo());
        processMapper.add(p);
        m.setHasTaxed(true);
        m.setTaxProcessId(p.getId());
        minerMapper.update(m);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, m.getSn());
        this.successFlow(f);
    }

    /**
     * The business logic of the trading of mining pledge
     *
     * @param f
     */
    @Transactional
    public void minerPledge(TradeFlow f) {
        Miner m = this.mineCheck(f, false, true);
        if (null == m)
            return;
        Process p = new Process();
        p.setType(Process.Type.MinerPledge.getValue())
                .setStage(Process.Stage.PledgeComplete.getValue())
                .setAddress(f.getFrom())
                .setSn(m.getSn())
                .setAmount(f.getTradeAmount())
                .setTradeNo(f.getTradeNo());
        processMapper.add(p);
        m.setHasPledged(true);
        m.setPledgeProcessId(p.getId());
        minerMapper.update(m);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, m.getSn());
        this.successFlow(f);
    }

    /**
     * The business logic of the trading of mining redemption
     *
     * @param f
     */
    @Transactional
    public void minerRedempt(TradeFlow f) {
        Miner m = this.mineCheck(f, false, false);
        if (null == m)
            return;
        Process p = new Process();
        p.setType(Process.Type.MinerRedemption.getValue())
                .setStage(Process.Stage.RedemptionComplete.getValue())
                .setAddress(f.getFrom())
                .setSn(m.getSn())
                .setAmount(f.getTradeAmount())
                .setTradeNo(f.getTradeNo());
        processMapper.add(p);
        m.setHasPledged(false);
        m.setPledgeProcessId(p.getId());
        minerMapper.update(m);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, m.getSn());
        this.successFlow(f);
    }

    /**
     * The business logic of the trading of wallet tax/pledge
     *
     * @param f
     */
    @Transactional
    public void walletPledge(TradeFlow f) {
        String wa = f.getFrom();
        if (org.apache.commons.lang3.StringUtils.isBlank(wa)) {
            log.error("Analysis of transaction flow -- error wallet tax/pledge: {}", f);
            this.errorFlow(f);
            return;
        }
        Wallet w = walletMapper.findOne(wa);
        if (w.isHasPledged()) {
            log.error("Analysis of transaction flow -- error repeatedly pays the flow of wallet tax/pledge: {}", f);
            this.errorFlow(f);
            return;
        }
        Process p = new Process();
        p.setType(Process.Type.WalletPledge.getValue())
                .setStage(Process.Stage.PledgeComplete.getValue())
                .setAddress(f.getFrom())
                .setAmount(f.getTradeAmount())
                .setTradeNo(f.getTradeNo());
        processMapper.add(p);
        w.setPledgeProcessId(p.getId());
        w.setHasPledged(true);
        w.setPromotionAddress(w.getAddress());
        walletMapper.update(w);// Update the wallet to the pledge and add it to your own mining pool
        // Wallet pledge has specific meanings, and needs to be extended additional
        // business content,
        // such as establishing a mining pool, developing API, etc.
        ApplyForPromotion apply = applyForPromotionMapper.find(wa);
        applyForPromotionMapper.updateStatus(wa, ApplyForPromotion.Status.PASS.get());
        Promotion promotion = promotionService.genPromotion(apply);// generic the promotion
        promotionService.add(promotion);
        // Add the mining machine under its jurisdiction to your own mining pool
        List<Miner> l = minerMapper.findByWalletAddress(w.getAddress());
        if (!CollectionUtils.isEmpty(l)) {
            for (Miner m : l) {
                m.setPromotionAddress(w.getPromotionAddress());
                minerMapper.update(m);
                jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, m.getSn());
            }
        }
        this.successFlow(f);
    }

}
