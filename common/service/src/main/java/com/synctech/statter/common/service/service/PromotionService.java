package com.statter.statter.common.service.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import com.statter.statter.base.entity.ApplyForPromotion;
import com.statter.statter.base.entity.Miner;
import com.statter.statter.base.entity.Promotion;
import com.statter.statter.base.entity.Wallet;
import com.statter.statter.base.mapper.MinerMapper;
import com.statter.statter.base.mapper.PromotionMapper;
import com.statter.statter.base.mapper.WalletMapper;
import com.statter.statter.base.vo.Page;
import com.statter.statter.constant.CacheKey;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.redis.config.vo.Hget;
import com.statter.statter.redis.jedis.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Slf4j
@Service
public class PromotionService {

    @Value("${statter.promotion.init.name.prefix:}")
    String promotionInitNamePrefix;

    @Autowired
    JedisService jedisService;

    @Autowired
    PromotionMapper promotionMapper;

    @Autowired
    WalletMapper walletMapper;

    @Autowired
    MinerMapper minerMapper;

    public Promotion add(Promotion promotion) {
        promotionMapper.add(promotion);
        return promotion;
    }

    public Promotion update(Promotion promotion) {
        promotionMapper.updateInfo(promotion);
        jedisService.hdel(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, promotion.getAddress());
        return get(promotion.getAddress());
    }

    public Page<Promotion> page(int page, int size, String kw) {
        Page<Promotion> r = new Page<>(page, size);
        kw = StringUtils.isBlank(kw) ? "" : StringUtils.trim(kw);
        int t = promotionMapper.count(kw);
        r.setTotal(t);
        if (t == 0 || r.startIndex() >= t) return r;
        List<Promotion> l = promotionMapper.page(r.startIndex(), r.getSize(), kw);
        r.setData(l);
        return r;
    }

    public Promotion get(String address) {
        Promotion promotion = jedisService.hget(
                new Hget<>(CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS, address, CacheKey.CACHEKEY_INFO_PROMOTION_BY_ADDRESS_LOCK, Promotion.class),
                p -> promotionMapper.findOne(p.getField()));
        if (null == promotion) {
            throw new AppBizException(HttpStatusExtend.ERROR_PROMOTION_NOT_FOUND, address);
        }
        return promotion;
    }

    public Promotion findByAlias(String alias) {
        Promotion promotion = promotionMapper.findOne(alias);
        if (null == promotion) {
            return null;
        }
        return promotion;
    }

    public long getHash(String address) {
        String v = jedisService.hget(CacheKey.CACHEKEY_HASH_INFO_PROMOTION, address);
        return StringUtils.isBlank(v) ? 0 : NumberUtil.parseLong(v);
    }

    public List<Wallet> listWallet(String address) {
        return walletMapper.findByPromotionAddress(address);
    }

    public List<Miner> listMiner(String address) {
        return minerMapper.findByPromotionAddress(address);
    }

    public Promotion genPromotion(ApplyForPromotion apply) {
        Promotion p = new Promotion();
        p.setAddress(apply.getAddress());
        int dbMaxCode = 100000;
        String dbMaxCodeStr = promotionMapper.maxCode();
        if (!StringUtils.isBlank(dbMaxCodeStr)) dbMaxCode = Integer.parseInt(dbMaxCodeStr);
        dbMaxCode++;
        p.setCode(dbMaxCode + "");
        p.setAlias(apply.getAlias());
        p.setIntroduction(apply.getIntroduction());
        p.setManagementKey(this.genHashKey());
        return p;
    }

    private String genHashKey() {
        return MD5.create().digestHex(RandomUtil.randomString(32) + System.currentTimeMillis() + RandomUtil.randomString(32));
    }

    public String refreshSecretKey(String a) {
        String newSecretKey = this.genHashKey();
        promotionMapper.updateSecretKey(a, newSecretKey);
        return newSecretKey;
    }


}
