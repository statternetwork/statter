package com.synctech.statter.base.entity;

import com.synctech.statter.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * It is used to record the process stage of "pledge", "redemption", and "mining tax"
 */
@Data
@Accessors(chain = true)
public class Process extends BaseEntity {

    /**
     * process type
     *
     * @see Type
     */
    byte type;
    /**
     * process stage
     *
     * @see Stage
     */
    byte stage;
    /**
     * Wallet associated with this process
     *
     * @see Wallet#address
     */
    String address;
    /**
     * Machine sn associated with this process.
     * If it is a mining pool tax, there is no value
     *
     * @see Miner#sn
     */
    String sn;

    /**
     * the trade flow num
     */
    String tradeNo;

    /**
     * This process amount
     */
    String amount;

    public enum Type {
        WalletPledge((byte) 11),
        //        WalletRedemption((byte) 12),
        MinerPledge((byte) 21),
        MinerRedemption((byte) 22),
        MinerTax((byte) 31),
        ;
        byte value;
        String desc;

        Type(byte v) {
            this.value = v;
        }

        public byte getValue() {
            return value;
        }

    }

    public enum Stage {
        PledgeApply((byte) 11),
        PledgeHasPayed((byte) 12),
        PledgeComplete((byte) 13),
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        RedemptionApply((byte) 21),
        RedemptionComplete((byte) 23),
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        TaxApply((byte) 31),
        TaxHasPayed((byte) 32),
        TaxComplete((byte) 33),
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /**
         * <p>end</p>
         * Used to mark delete
         */
        Complete((byte) 99),
        ;
        byte value;

        Stage(byte v) {
            this.value = v;
        }

        public byte getValue() {
            return value;
        }

        public boolean compare(byte s) {
            return this.value == s;
        }

    }

}
