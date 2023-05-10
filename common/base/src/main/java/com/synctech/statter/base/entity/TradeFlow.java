package com.synctech.statter.base.entity;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Transaction flow
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TradeFlow implements Serializable {

    /**
     * Transaction number
     */
    String tradeNo;
    /**
     * Transaction type
     *
     * @see TradeType
     */
    String tradeType;
    /**
     * Sub transaction type
     *
     * @see TradeSubType
     */
    @JsonProperty("type")
    String tradeSubType;

    /**
     * Payment address
     */
    String from;
    /**
     * Receipt address
     */
    String to;

    /**
     * Transaction time
     */
    String tradeTime;
    /**
     * Transaction amount
     */
    String tradeAmount;
    /**
     * gas
     */
    String gas;

    /**
     * Transaction public key
     */
    String publicKey;

    /**
     * Transaction sign
     */
    String sign;

    /**
     * the mining machine sn
     */
    String uniqueCode;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * ???????????????
     */
    String contractNumber;
    /**
     * ?????????????
     */
    String machineCode;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The block of this transaction flow
     */
    long blockIndex;
    /**
     * Transaction stage
     *
     * @see Stage
     */
    byte stage;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public enum TradeType {

        Tax("f", "tax"), Pledge("g", "pledge"),
        ;
        String value;
        String desc;

        TradeType(String v, String desc) {
            this.value = v;
            this.desc = desc;
        }

        public String getValue() {
            return value;
        }

        public boolean compare(String s) {
            return StringUtils.equals(this.value, s);
        }

    }

    public enum TradeSubType {

        MinerPledge("0", "miner pledge"), MinerRedempt("1", "miner redemption"),
        ;
        String value;
        String desc;

        TradeSubType(String v, String desc) {
            this.value = v;
            this.desc = desc;
        }

        public String getValue() {
            return value;
        }

        public boolean compare(String s) {
            return StringUtils.equals(this.value, s);
        }

    }

    public enum Stage {
        Storage((byte) 0, "Storage with no processed"), Success((byte) 1, "Analyze success"), Failed((byte) 2, "Analyze failed"),
        ;
        byte value;
        String desc;

        Stage(byte v, String desc) {
            this.value = v;
            this.desc = desc;
        }

        public byte getValue() {
            return value;
        }

        public boolean compare(byte s) {
            return this.value == s;
        }
    }


}
