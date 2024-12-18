package com.statter.statter.administrator.api.controller.v1.version.vo;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

@Data
@Accessors(chain = true)
public class ReqMinerVersionAddNewVer {

    /**
     * the script url what is the statter-update ask for.
     * example: http://192.168.1.164/downloads/statter.update.v1.0.7.sh
     */
    String updateScriptUrl;

    /**
     * the valid seqver array.
     * in this array, every element is one valid seqver, used to allow miner update delay
     */
    int[] vers;

    public boolean validate() {
        if (StringUtils.isBlank(this.getUpdateScriptUrl()) || ArrayUtil.isEmpty(this.getVers())) {
            return false;
        }
        return true;
    }

}
