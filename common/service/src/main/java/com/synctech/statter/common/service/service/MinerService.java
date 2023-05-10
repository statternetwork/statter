package com.synctech.statter.common.service.service;

import com.synctech.statter.base.entity.Miner;
import com.synctech.statter.base.mapper.MinerMapper;
import com.synctech.statter.base.mapper.ProcessMapper;
import com.synctech.statter.base.mapper.WalletMapper;
import com.synctech.statter.common.service.vo.info.Hash;
import com.synctech.statter.common.service.vo.info.MinerVo;
import com.synctech.statter.constant.CacheKey;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.redis.config.vo.Hget;
import com.synctech.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MinerService {

    @Autowired
    JedisService jedisService;

    @Autowired
    MinerMapper minerMapper;

    @Autowired
    ProcessMapper processMapper;

    @Autowired
    WalletMapper walletMapper;

    @Autowired
    ProcessService processService;

    @Autowired
    HashService hashService;

    public void add(String sn) {
        Miner m = new Miner();
        m.setSn(sn);
        minerMapper.add(m);
    }

    /**
     * find all miner(no cache)
     *
     * @return
     */
    public List<Miner> findAll() {
        return minerMapper.findAll();
    }

    /**
     * Get the mining machine information and use the cache priority
     *
     * @param sn
     * @return
     */
    public MinerVo findBySn(String sn) {
        return jedisService.hget(new Hget<>(CacheKey.CACHEKEY_INFO_MINER_BY_SN, sn, CacheKey.CACHEKEY_INFO_MINER_BY_SN_LOCK, MinerVo.class), p -> findBySnImpl(sn));
    }

    private MinerVo findBySnImpl(String sn) {
        Miner po = minerMapper.findOne(sn);
        if (null == po) {
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_NOT_FOUND);
        }
        MinerVo vo = new MinerVo(po);
        processService.query(vo);
        return vo;
    }

    public List<MinerVo> findByWallet(String address) {
        List<Miner> miners = minerMapper.findByWalletAddress(address);
        return getMinerVos(miners);
    }


//    public List<MinerVo> findByPromotion(String address) {
//        Promotion p = promotionMapper.findOne(address);
//        if (null == p) {
//            throw new AppBizException(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND);
//        }
//        List<Miner> miners = minerMapper.findByPromotionAddress(address);
//        return getMinerVos(miners);
//    }

    @NotNull
    private List<MinerVo> getMinerVos(List<Miner> miners) {
        List<MinerVo> minerVos = new ArrayList<>();
        for (Miner po : miners) {
            MinerVo vo = new MinerVo(po);
            Hash hash = hashService.get(po.getSn());
            vo.setHash(hash.getH()).setOnline(hash.isOnline());
            processService.query(vo);
            minerVos.add(vo);
        }
        return minerVos;
    }


    public void add(Miner miner) {
        Miner mi = minerMapper.findOne(miner.getSn());
        if (null != mi) {
            throw new AppBizException(HttpStatusExtend.CREATED);
        }
        minerMapper.add(miner);
    }

    public void updateMinerWallet(String sn, String addr) {
        minerMapper.updateWalletAddress(sn, addr);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, sn);
    }

    public void updateMinerMachineIdAndCpu(String sn, String machineId, String cm) {
        minerMapper.updateMachineId(sn, machineId, cm);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, sn);
    }

    public void updateMiner(Miner miner) {
        Miner min = minerMapper.findOne(miner.getSn());
        if (null == min) {
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_NOT_FOUND);
        }
        if (StringUtils.hasText(miner.getWalletAddress())) {
            min.setWalletAddress(miner.getWalletAddress());
        }
        if (StringUtils.hasText(miner.getPromotionAddress())) {
            min.setPromotionAddress(miner.getPromotionAddress());
        }
        if (miner.isHasPledged() != min.isHasPledged()) {
            min.setHasPledged(miner.isHasPledged());
        }
        if (0 != miner.getPledgeProcessId()) {
            min.setPledgeProcessId(miner.getPledgeProcessId());
        }
        if (miner.isHasTaxed() != min.isHasTaxed()) {
            min.setHasTaxed(miner.isHasTaxed());
        }
        if (0 != miner.getTaxProcessId()) {
            min.setTaxProcessId(miner.getTaxProcessId());
        }
        minerMapper.update(min);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // flush the cache of this miner
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, miner.getSn());
    }


    public void updateMinerPa(String sn, String pa) {
        Miner miner = new Miner();
        miner.setSn(sn);
        miner.setPromotionAddress(pa);
        this.updateMiner(miner);
    }

    public void updateMinerHp(Miner miner) {
        Miner m = minerMapper.findOne(miner.getSn());
        if (null == m) {
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_NOT_FOUND);
        }
        if (m.isHasPledged() != miner.isHasPledged()) {
            minerMapper.updateHp(miner);
        }
    }

    public void updateMinerPpi(String sn, Long ppi) {
        Miner m = minerMapper.findOne(sn);
        if (null == m) {
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_NOT_FOUND);
        }
        Miner miner = new Miner();
        miner.setSn(sn);
        miner.setPledgeProcessId(ppi);
        minerMapper.updatePpi(miner);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, sn);
    }


}
