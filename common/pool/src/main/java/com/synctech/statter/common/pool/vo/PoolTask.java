package com.statter.statter.common.pool.vo;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import com.statter.statter.util.HexUtil;
import com.statter.statter.util.Sha256Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.stringtemplate.v4.ST;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Data
public class PoolTask {

    int status;
    String blockHash;
    String workload;
    Block block;


    public boolean validate(String randomNum) {
        if (!NumberUtil.isNumber(randomNum)) {
            return false;
        }
        // step 1: generate the meta string  =>  "<blockHash><path><createTime><randomNumber>"
        ST st1 = new ST("<blockHash><path><createTime><randomNumber>");
        st1.add("blockHash", this.blockHash);
        st1.add("path", this.block.path);
        st1.add("createTime", this.block.createTime);
        st1.add("randomNumber", randomNum);
        String metaStr = st1.render();
        log.trace("report meta data: {}", metaStr);
        // step 2: encrypt with md5
        String metaMd5Str = SecureUtil.md5().digestHex(metaStr);
        log.trace("report meta data with md5: {}", metaMd5Str);
        // step 3: generate the salt  =>  Block [blockIndex=<blockIndex>, headHash=<headHash>, randomNumber=<randomNumber>, path=<path>, createTime=<createTime>, endHash=<endHash>]"
        ST st2 = new ST("Block [blockIndex=<blockIndex>, headHash=<headHash>, randomNumber=<randomNumber>, path=<path>, createTime=<createTime>, endHash=<endHash>]");
        st2.add("blockIndex", this.block.blockIndex);
        st2.add("headHash", this.block.headHash);
        st2.add("randomNumber", randomNum);
        st2.add("path", this.block.path);
        st2.add("createTime", this.block.createTime);
        st2.add("endHash", metaMd5Str);
        String saltStr = st2.render();
        log.trace("report salt: {}", saltStr);
        // step 4: Use sha256 to encrypt the blockhash with the salt, and hex the result to a string
        try {
            byte[] sha256Bytes = Sha256Util.encode(this.blockHash.getBytes(), saltStr.getBytes());
            String hexStr = HexUtil.hexStr(sha256Bytes);
            log.trace("report result hex string: {}", hexStr);
            // step 5: check the workload
            if (StringUtils.startsWith(hexStr, this.workload)) {
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
        }
        return false;
    }

}
