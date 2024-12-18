package com.statter.statter.common.service.service;

import cn.hutool.core.bean.BeanUtil;
import com.statter.statter.base.entity.Miner;
import com.statter.statter.base.entity.Wallet;
import com.statter.statter.base.mapper.MinerMapper;
import com.statter.statter.base.mapper.WalletMapper;
import com.statter.statter.common.service.vo.info.WalletVo;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.redis.config.vo.Hget;
import com.statter.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import static com.statter.statter.constant.Constant.checkWalletAddress;

@Slf4j
@Service
public class WalletService {

    @Autowired
    JedisService jedisService;

    @Autowired
    MinerMapper minerMapper;

    @Autowired
    MinerService minerService;

    @Autowired
    WalletMapper walletMapper;

    @Autowired
    ProcessService processService;

    public WalletVo findByAddress(String address) {
        Hget<WalletVo> hget = new Hget<>(CacheKey.CACHEKEY_INFO_WALLET_BY_ADDRESS, address, CacheKey.CACHEKEY_INFO_WALLET_BY_ADDRESS_LOCK, WalletVo.class);
        WalletVo r = jedisService.hget(hget, p -> {
            checkWalletAddress(p.getField());
            Wallet w = walletMapper.findOne(p.getField());
            if (null == w) {
                w = new Wallet();
                w.setAddress(p.getField());// regist a new address into db
                walletMapper.add(w);
            }
            WalletVo vo = BeanUtil.toBean(w, WalletVo.class);
            processService.processWalletPledge(vo);
            return vo;
        });
        return r;
    }

    public void update(Wallet w) {
        walletMapper.update(w);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_WALLET_BY_ADDRESS, w.getAddress());
    }

    @Transactional
    public void updateWalletPromotion(String wa, String pa) {
        walletMapper.updateWalletPromotion(wa, pa);
        log.info("updateWalletPromotion[66]---------------clear wallet info cache");
        jedisService.hdel(CacheKey.CACHEKEY_INFO_WALLET_BY_ADDRESS, wa);
        List<Miner> l = minerMapper.findByWalletAddress(wa);
        if (CollectionUtils.isEmpty(l)) return;
        for (Miner m : l) {
            if (StringUtils.isNotBlank(m.getPromotionAddress())) {
                if (!StringUtils.equals(m.getPromotionAddress(), pa))
                    throw new AppBizException(HttpStatusExtend.ERROR_WALLET_SELECTED_PROMOTION_MINER_EXIST_PROMOTION);
            }
        }
        minerMapper.updatePromotionByWalletAddress(wa, pa);
        for (Miner m : l) {
            try {
                log.info("updateWalletPromotion[79]---------------clear miner info cache");
                minerService.refreshCache(m.getSn());
            } catch (Exception e) {
            }
        }
        log.info("updateWalletPromotion[84]---------------clear wallet miners list cache");
        jedisService.hdel(CacheKey.CACHEKEY_LIST_MINERS_BY_WALLET_ADDRES, wa);
    }

    public void updateWalletPpi(String wa, long ppi, boolean hasPledged) {
        walletMapper.updatePpi(wa, ppi, hasPledged);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_WALLET_BY_ADDRESS, wa);
    }
}
