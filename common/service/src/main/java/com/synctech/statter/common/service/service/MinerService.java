package com.statter.statter.common.service.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import com.statter.statter.base.entity.Miner;
import com.statter.statter.base.mapper.MinerMapper;
import com.statter.statter.base.mapper.ProcessMapper;
import com.statter.statter.base.mapper.WalletMapper;
import com.statter.statter.common.service.vo.info.Hash;
import com.statter.statter.common.service.vo.info.MinerVo;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.redis.config.vo.Hget;
import com.statter.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MinerService {

    @Autowired
    JedisService jedisService;

    @Autowired
    MinerMapper minerMapper;

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
     * Get the mining machine information and use the cache priority
     *
     * @param sn
     * @return
     */
    public MinerVo findBySn(String sn) {
        try {
            return jedisService.hget(CacheKey.CACHEKEY_INFO_MINER_BY_SN, sn, MinerVo.class);
        } catch (Exception e) {
            log.error("find miner info error[{}]: {}", sn, e.getMessage());
            throw e;
        }
    }

    public MinerVo refreshCache(String sn) {
        jedisService.hdel(CacheKey.CACHEKEY_INFO_MINER_BY_SN, sn);
        MinerVo vo = findBySnImpl(sn);
        if (null == vo) return null;
        jedisService.hset(CacheKey.CACHEKEY_INFO_MINER_BY_SN, sn, JSONObject.toJSONString(vo));
        return vo;
    }

    private MinerVo findBySnImpl(String sn) {
        Miner m = minerMapper.findOne(sn);
        if (null == m) return null;
        MinerVo vo = new MinerVo(m);
        processService.query(vo);
        if (null == vo.getLeaveFactory()) {
            vo.setLeaveFactory(new Timestamp(System.currentTimeMillis()));
            minerMapper.updateLeaveFactory(vo);
        }
        return vo;
    }

    public List<MinerVo> findByWallet(String address) {
        List<Miner> pos = minerMapper.findByWalletAddress(address);
        if (CollectionUtil.isEmpty(pos)) pos = new ArrayList<>();
        return pos.stream().map(po -> {
            MinerVo vo = findBySn(po.getSn());
            hashService.queryMiner(vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateMinerWallet(String sn, String wa, String pa) {
        minerMapper.updateWalletAddress(sn, wa);
        if (StringUtils.isNotBlank(wa) && StringUtils.isNotBlank(pa))
            minerMapper.updatePromotionAddress(sn, pa);
        cleanCache(sn, null);
    }

    public void updateMinerMachineIdAndCpu(String sn, String machineId, String cm) {
        minerMapper.updateMachineId(sn, machineId, cm);
        cleanCache(sn, null);
    }

    public void cleanCache(String sn, String wa) {
        if (StringUtils.isNotBlank(sn))
            refreshCache(sn);
        if (StringUtils.isNotBlank(wa))
            jedisService.hdel(CacheKey.CACHEKEY_LIST_MINERS_BY_WALLET_ADDRES, wa);
    }

//    public void updateMinerHp(Miner miner) {
//        Miner m = minerMapper.findOne(miner.getSn());
//        if (null == m) {
//            throw new AppBizException(HttpStatusExtend.ERROR_MINER_NOT_FOUND, miner.getSn());
//        }
//        if (m.isHasPledged() != miner.isHasPledged()) {
//            minerMapper.updateHp(miner);
//        }
//        cleanCache(miner.getSn(), miner.getWalletAddress());
//    }

    public void updateMinerPpi(String sn, Long ppi) {
        Miner m = minerMapper.findOne(sn);
        if (null == m) {
            throw new AppBizException(HttpStatusExtend.ERROR_MINER_NOT_FOUND, sn);
        }
        Miner miner = new Miner();
        miner.setSn(sn);
        miner.setPledgeProcessId(ppi);
        minerMapper.updatePpi(miner);
        cleanCache(miner.getSn(), miner.getWalletAddress());
    }

}
