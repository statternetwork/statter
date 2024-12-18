package com.statter.statter.administrator.api.controller.v1.tax.vo;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ReqTaxRule {

    String amount;

    String targetAddress;

    public boolean validate() {
        if (StringUtils.isBlank(this.amount) || !NumberUtil.isNumber(this.amount)) {
            return false;
        } else if (ArrayUtil.isEmpty(this.targetAddress) || !Pattern.matches("^ST[0-9a-zA-Z]{32}$", this.targetAddress)) {
            return false;
        }
        return true;
    }

}
