package com.statter.statter.base.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ledger
 */
@Data
@Accessors(chain = true)
public class Ledger implements Serializable {

    /**
     * <p>ID</p>
     * use {@link java.util.UUID} to generate the id
     */
    String id;
    /**
     * block height
     */
    long blockIndex;
    /**
     * ledger creating time
     */
    Timestamp createdTime;
    /**
     * ledger state
     *
     * @see State
     */
    byte state;
    /**
     * the sn code of the miner who computed the correct result first
     */
    String sn;
    /**
     * the wallet address of the miner who computed the correct result first
     */
    String address;
    /**
     * the earning belong to
     */
    String promotionAddress;
    /**
     * the profit to the promotion on this block
     */
    String mingProfit;
    /**
     * ledger data
     */
    byte[] data;

    /**
     * ledger state constant
     */
    public static enum State {
        /**
         * <p>storage state</p>
         * only storage, the origin state when this record save into db
         */
        Storage((byte) 1),
        /**
         * <p>invalid state</p>
         * the ledgers on the block index will be change to invalid state, after a ledger be choosed to set valid.
         * the invalid ledgers will be clean periodic.
         */
        Invalid((byte) 2),
        /**
         * <p>valid state</p>
         * Extracted and identified effective ledgers will be storaged in a long time,they are the distribution basis.
         * In addition, the ledger contains all miner's workload in the global network,so, it must be deal with the biz.
         * if the earning is exclusive to the pool, need to advance filter by the sn,wallet address,promotion address.
         */
        Valid((byte) 3),
        /**
         * <p>archive state</p>
         * Because the data amount of the ledge will be increased
         * Due to the particularity of the ledger data, its DATA module is very large,
         * and it cannot be stored in the database for a long time.
         * Therefore, it is necessary to regularly archive an effective ledger.
         * Database space and improve database efficiency.
         */
        Archive((byte) 4),
        ;

        private final byte value;

        State(byte v) {
            this.value = v;
        }

        public byte getValue() {
            return this.value;
        }
    }

}
