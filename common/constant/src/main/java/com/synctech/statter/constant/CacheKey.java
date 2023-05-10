package com.synctech.statter.constant;

public abstract class CacheKey {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INFO
    /**
     *
     */
    public static final String CACHEKEY_INFO_MINER_SN_MAP = "CACHEKEY_INFO_MINER_SN_MAP";
    /**
     *
     */
    public static final String CACHEKEY_INFO_CPU_MODEL = "CACHEKEY_INFO_CPU_MODEL";
    /**
     * <p>the key:miner info by sn</p>
     * <p>the lock:miner info by sn</p>
     * <p>cache type:hash. the field is miner SN, the value is the miner info</p>
     */
    public static final String CACHEKEY_INFO_MINER_BY_SN = "CACHEKEY_INFO_MINER_BY_SN",
            CACHEKEY_INFO_MINER_BY_SN_LOCK = "CACHEKEY_INFO_MINER_BY_SN_LOCK";
    /**
     * <p>the key:wallet info by address</p>
     * <p>the lock:wallet info by address</p>
     * <p>cache type:hash,the field is the wallet address,the value is the wallet info</p>
     */
    public static final String CACHEKEY_INFO_WALLET_BY_ADDRESS = "CACHEKEY_INFO_WALLET_BY_ADDRESS",
            CACHEKEY_INFO_WALLET_BY_ADDRESS_LOCK = "CACHEKEY_INFO_WALLET_BY_ADDRESS_LOCK";
    /**
     * <p>the key:promotion info by address</p>
     * <p>the lock:promotion info by address</p>
     * <p>cache type:hash,the field is the promotion address,the value is the promotion info</p>
     */
    public static final String CACHEKEY_INFO_PROMOTION_BY_ADDRESS = "CACHEKEY_INFO_PROMOTION_BY_ADDRESS",
            CACHEKEY_INFO_PROMOTION_BY_ADDRESS_LOCK = "CACHEKEY_INFO_PROMOTION_BY_ADDRESS_LOCK";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // mining
    /**
     * <p>the key:the global block index height</p>
     * <p>the lock:the global block index height</p>
     * <p>cache type:String,the value is the block index</p>
     */
    public static final String CACHEKEY_MINING_BLOCK_INDEX = "CACHEKEY_MINING_BLOCK_INDEX";
    /**
     * <p>the key prefix:the block task</p>
     * <p>the lock:block task</p>
     * <p>cache type:hash,the suffix of the key is the block index,the field is the wallet address,
     * the value is the task in this block index</p>
     */
    public static final String CACHEKEY_MINING_QUESTION_KEY_PREFIX = "CACHEKEY_MINING_QUESTION_KEY_PREFIX",
            CACHEKEY_MINING_QUESTION_LOCK = "CACHEKEY_MINING_QUESTION_LOCK";
    /**
     * <p>the key prefix:the ledger on the block index</p>
     * <p>the lock:the ledger on the block index</p>
     * <p>cache type:hash,the suffix of the key is the block index,the field is the machine sn,
     * the value is the ledger in this block index</p>
     */
    public static final String CACHEKEY_MINING_LEDGER_KEY_PREFIX = "CACHEKEY_MINING_LEDGER_KEY_PREFIX",
            CACHEKEY_MINING_LEDGER_LOCK = "CACHEKEY_MINING_LEDGER_LOCK";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // hash
    /**
     * <p>the key:miner hash</p>
     * <p>cache type:hash, the field is the miner sn, the value is the miner hash</p>
     */
    public static final String CACHEKEY_HASH_INFO_MINER = "CACHEKEY_HASH_INFO_MINER";
    /**
     * <p>the key:wallet hash</p>
     * <p>cache type:hash, the field is the wallet address, the value is the miner hash</p>
     */
    public static final String CACHEKEY_HASH_INFO_WALLET = "CACHEKEY_HASH_INFO_WALLET";

    /**
     * <p>the key:pool hash</p>
     * <p>cache type:hash, the field is the pool, the value is the miner hash</p>
     */
    public static final String CACHEKEY_HASH_INFO_PROMOTION = "CACHEKEY_HASH_INFO_PROMOTION";
    /**
     * <p>the key:global hash</p>
     * <p>cache type:String(long value)</p>
     */
    public static final String CACHEKEY_HASH_INFO_GLOBAL = "CACHEKEY_HASH_INFO_GLOBAL";
    /**
     * <p>the key:the update time of the global hash</p>
     * <p>cache type:String(long value)</p>
     * <p>Used to auxiliary distributed operations to avoid repeated computing computing power</p>
     */
    public static final String CACHEKEY_HASH_INFO_GLOBAL_CACHE_TIME = "CACHEKEY_HASH_INFO_GLOBAL_CACHE_TIME";
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // ADMIN
    /**
     * <p>the key:biz rule</p>
     * <p>cache type:hash, the field is the rule type, the value is the content</p>
     */
    public static final String CACHEKEY_ADMIN_RULE_BY_TYPE = "CACHEKEY_ADMIN_RULE_BY_TYPE",
            CACHEKEY_ADMIN_RULE_BY_TYPE_LOCK = "CACHEKEY_ADMIN_RULE_BY_TYPE_LOCK";

    /**
     * <p>the key:miner white list</p>
     * <p>cache type:hash, the field is the miner sn, the value is boolean value</p>
     */
    public static final String CACHEKEY_ADMIN_WHITE_LIST_MINER = "CACHEKEY_ADMIN_WHITE_LIST_MINER";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // POOL API
    /**
     * <p>the key:promotion ak info by address</p>
     * <p>cache type:hash,the field is the promotion address,the value is the ak refresh count and timestamp</p>
     * value : {'c':1, 'ct':111, 'etc':111}
     */
    public static final String CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS = "CACHEKEY_AK_PROMOTION_REFRESH_BY_ADDRESS";
    /**
     * <p>the key:promotion ak</p>
     * <p>cache type:hash,the field is the promotion access token,the value is the promotion info</p>
     */
    public static final String CACHEKEY_AK_PROMOTION_BY_AK = "CACHEKEY_AK_PROMOTION_BY_AK";

    /**
     * <p>the key:promotion addr</p>
     * <p>cache type:hash,the field is the promotion access token,the value is the list of the block ledger</p>
     */
    public static final String CACHEKEY_LEDGER_LIST_BY_PROMOTION = "CACHEKEY_LEDGER_LIST_BY_PROMOTION";
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
