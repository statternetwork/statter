package com.synctech.statter.common.service.service;

import cn.hutool.core.bean.BeanUtil;
import com.synctech.statter.base.entity.Wallet;
import com.synctech.statter.base.mapper.MinerMapper;
import com.synctech.statter.base.mapper.WalletMapper;
import com.synctech.statter.common.service.vo.info.WalletVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.synctech.statter.constant.Constant.checkWalletAddress;

@Slf4j
@Service
public class WalletService {

    @Autowired
    MinerMapper minerMapper;

    @Autowired
    WalletMapper walletMapper;

    @Autowired
    ProcessService processService;

    /*private void checkWalletAddress(String a) {
        String regex = "^stt[0-9a-zA-Z]{32}$";
        if (Pattern.matches(regex, a)) {
            return;
        }
        throw new AppBizException(HttpStatusExtend.ERROR_WALLET_INVALID_ADDRESS);
    }*/

    public WalletVo findByAddress(String address) {
        checkWalletAddress(address);
        Wallet w = walletMapper.findOne(address);
        if (null == w) {
            w = new Wallet();
            w.setAddress(address);// regist a new address into db
            walletMapper.add(w);
        }
        WalletVo vo = BeanUtil.toBean(w, WalletVo.class);
        processService.processWalletPledge(vo);
        return vo;
    }

    public void update(Wallet w) {
        walletMapper.update(w);
    }

    public void updateWalletPromotion(String wa, String pa) {
        walletMapper.updateWalletPromotion(wa, pa);
    }

    public void updateWalletPpi(String wa, long ppi, boolean hasPledged) {
        walletMapper.updatePpi(wa, ppi, hasPledged);
    }
}
