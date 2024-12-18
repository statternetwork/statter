package com.statter.statter.constant;

import com.statter.statter.constant.restful.AppBizException;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public interface Constant {

    int WALLET_ADDRESS_LENGTH = 35;
    String WALLET_ADDRESS_REGEX = "^stt[0-9a-zA-Z]{32}$";

    static void checkWalletAddress(String a) {
        if (StringUtils.isNotBlank(a) && Pattern.matches(WALLET_ADDRESS_REGEX, a)) return;
        throw new AppBizException(HttpStatusExtend.ERROR_WALLET_INVALID_ADDRESS);
    }

}
