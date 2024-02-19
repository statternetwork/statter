package com.synctech.statter.constant;


/**
 * org.springframework.http.HttpStatus
 */
public enum HttpStatusExtend {

    // 1xx Informational
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),
    CHECKPOINT(103, "Checkpoint"),

    // 2xx Success
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MULTI_STATUS(207, "Multi-Status"),
    ALREADY_REPORTED(208, "Already Reported"),
    IM_USED(226, "IM Used"),

    // 3xx Redirection
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    MOVED_TEMPORARILY(302, "Moved Temporarily"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "Permanent Redirect"),

    // --- 4xx Client Error ---
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"), GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    URI_TOO_LONG(414, "URI Too Long"),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    I_AM_A_TEAPOT(418, "I'm a teapot"),
    INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space On Resource"),
    METHOD_FAILURE(420, "Method Failure"),
    DESTINATION_LOCKED(421, "Destination Locked"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    LOCKED(423, "Locked"),
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),

    // --- 5xx Server Error ---
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    LOOP_DETECTED(508, "Loop Detected"),
    BANDWIDTH_LIMIT_EXCEEDED(509, "Bandwidth Limit Exceeded"),
    NOT_EXTENDED(510, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"),
    ERROR_INVALID_REQUEST(521, "INVALID REQUEST"),// invalid request,for validating or interceptin or malicious request

    // customize biz response code is a five digit, for example,10001,20001
    // system framework response code is a four digit, for example,1001,2001
    // use different start number by each biz module
    // naming requirement:use "ERROR" or "WARN" as the start chars,then append module name ,
    // and the specific function,
    // make sure the name directly reflects your means.

    // --- ---
    ERROR_COMMON(1001, "Internal error, please contact the manager"),// common sys error
    ERROR_COMMON_FOUND_NO_SERVER(1002, "No specified service is found"),
    ERROR_COMMON_DEV_WRONG_PARAM_TYPE(1002, "Development error: wrong parameter type"),
    ERROR_COMMON_DEV_PARAM_IS_NONE(1003, "Development error: miss parameters"),
    ERROR_COMMON_DEV_PARAM_CONVERT(1004, "Development error: type convert error"),
    ERROR_COMMON_DEV_DB_WRONG_CLAZZ(1007, "Development error: wrong db class bean"),
    ERROR_COMMON_DEV_NO_COOKIE(1008, "Development error: no cookie"),
    ERROR_COMMON_FOUND_NONE(1009, "Internal error: not found"),
    // errors when file operations
    ERROR_FILE_UPLOAD_NO_URI(2001, "File upload error: The path cannot be empty"),
    ERROR_FILE_READ_NO_ID(2002, "File read error: file ID cannot be empty"),
    ERROR_FILE_NOT_FOUND(2003, "File read error: no file is found"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // --- 1xxxx auth error ---
    ERROR_AUTH_USER_NOT_FOUND(10001, "Auth error: user not found"),
    ERROR_AUTH_USER_WRONG_PASSWORD(10002, "Auth error: wrong password"),
    ERROR_AUTH_USER_HAS_NO_AUTHORITY(10003, "Auth error: no authority"),
    ERROR_AUTH_AUTHRIZE_FAILED(10004, "Auth error: authenticate failed"),
    ERROR_AUTH_TOKEN_GET(10005, "Auth error: error when get token"),
    ERROR_AUTH_TOKEN_REFRESH(10006, "Auth error: error when refresh token"),
    ERROR_AUTH_TOKEN_CHECK(10007, "Auth error: error when check token"),
    ERROR_AUTH_TOKEN_CHECK_INVALID_TOKEN(10008, "Auth error: invalid token"),
    ERROR_AUTH_TOKEN_CHECK_NO_AUTHORIZATION_IN_HEAD(10009, "Auth error: not login"),

    // --- 11xxx pool error ---
    ERROR_POOL_GET_BLOCK_INDEX(10001, "Gateway error: error occurs when obtaining the block height"),
    ERROR_POOL_GET_POOL_TASK(10002, "Gateway error: error when fetch mining task"),
    ERROR_POOL_CANNT_GET_JEDIS_RESOURCE(10004, "Gateway error: cannot get redis resource"),
    ERROR_POOL_CANNT_GET_REDIS_LOCK(10005, "Gateway error: cannot get redis lock"),
    ERROR_POOL_COMMIT_RESULT(10006, "Gateway error: error occurs when the calculation result is submitted to the gateway"),
    ERROR_POOL_DOWNLOAD_BLOCK(10007, "Gateway error: wrong block content"),
    ERROR_POOL_DOWNLOAD_BLOCK_EMPTY(10008, "Gateway error: block content is empty"),
    ERROR_POOL_GET_POOL_TASK_NOT_MATCH_BLOCK_INDEX(10009, "Gateway error: the block height is different between the task and the block height"),
    ERROR_POOL_GET_POOL_TASK_EMPTY(10010, "Gateway error: the pool task is empty"),
    ERROR_POOL_COMMIT_RESULT_EXPIRE_BLOCKINDEX(10011, "Gateway error: block index is expired"),

    // --- 21xxx miner manage  ---
    ERROR_MINER_NOT_FOUND(20001, "Miner manage error: miner not found: sn = {}"),
    ERROR_WALLET_INVALID_ADDRESS(20002, "Miner manage error: wrong wallet address"),
    ERROR_PROMOTION_NOT_FOUND(20003, "Miner manage error: promotion not found"),
    ERROR_MINER_HAS_BIND_WALLET(20004, "Miner manage error: miner has bind wallet"),
    ERROR_MINER_BIND_WALLET_PROMOTION_ADDRESS_NOT_MATCH(20005, "Miner manage error: cannot change promotion"),
    ERROR_MINER_SN_CODE(20006, "Miner manage error: invalid sn code"),
    ERROR_MINER_UNREGISTERED_MACHINE(20007, "Miner manage error: unregistered machine"),
    ERROR_MINER_BIND_WALLET_PROMOTION_ADDRESS_IS_BLANK(20015, "Miner manage error: wallet has not select promotion"),
    ERROR_MINER_UNBIND_WALLET(20006, "Miner manage error: error when miner unbind wallet"),
    ERROR_WALLET_SELECTED_PROMOTION_EXIST(20007, "Wallet manage error: miner has binded wallet, you cannot choose again"),
    ERROR_WALLET_SELECTED_PROMOTION_MINER_EXIST_PROMOTION(20008, "Wallet manage error: the promotion of your miner under you wallet is different with the promotion you select "),
    ERROR_WALLET_APPLY_FOR_PROMOTION_NOT_FOUND(21001, "Wallet mange error: the apply for promotion is none"),
    ERROR_WALLET_APPLY_FOR_PROMOTION_EXIST(21002, "Wallet mange error: the apply for promotion is existed"),
    ERROR_WALLET_ALIAS_FOR_PROMOTION_EXIST(21003, "Wallet mange error: the alias for promotion is existed"),
//    ERROR_MINER_TAX_HAS_TAXED(20009, "Miner manage error: has taxed"),
//    ERROR_MINER_TAX_NOT_COMPLETE(20010, "Miner manage error: the progress of miner tax has not completed,cannot apply again"),
//    ERROR_MINER_PLEDGE_HAS_PLEDGED(20011, "Miner manage error: has pledged"),
//    ERROR_MINER_PLEDGE_NOT_COMPLETE(20012, "Miner manage error: the progress of miner pledge has not completed,cannot apply again"),
//    ERROR_MINER_REDEMPT_NO_PLEDGE(20014, "Miner manage error: there is no pledge on this miner,cannot apply redemption"),
//    ERROR_MINER_WALLET_IS_BLANK(20016, "Miner manage error: miner has not binded wallet"),
//    ERROR_WALLET_PLEDGE_UNDER_POOL(20020, "Miner manage error: the wallet address is belong to another promotion, cannot apply wallet tax"),
//    ERROR_WALLET_PLEDGE_HAS_PLEDGED(20021, "Miner manage error: has taxed"),
//    ERROR_WALLET_PLEDGE_NOT_COMPLETE(20022, "Miner manage error: the progress of miner tax has not completed,cannot apply again"),

    // --- 31xxx mining  ---
    ERROR_MINING_REPORT_WRONG_BLOCK_INDEX(30002, "Mining error: wrong block height"),
    ERROR_MINING_REPORT_WRONG_QUESTION(30003, "Mining error: wrong mining task"),
    ERROR_MINING_REPORT_WRONG_COMPUTE_RESULT(30004, "Mining error: wrong computing result "),
    ERROR_MINING_TASK_NO_WALLET(30005, "Mining error: has not binded wallet address"),
    ERROR_MINING_REPORT_LEDGER_NOT_FOUND_BY_BLOCKINDEX(30006, "Mining error: the ledger by the block height not found"),
    ERROR_MINING_LEDGER_NOT_FOUND(30007, "Mining error: the ledge not found"),
    ERROR_MINING_MINER_CANNOT_MINING(30008, "Mining error: the miner does not meet the mining conditions"),
    ERROR_MINING_MINER_VERSION_EXPIRED(30008, "Mining error: the version of the miner is expired"),
    ERROR_MINING_LEDGER_SN_QUANTITY_NOT_ENOUGH(30009, "Mining error: the quantity of the ledger sn is not enough"),

    // --- 4xxxx pool api   ---
    ERROR_POOL_API_SECRET_KEY_REFRESH_ONCE_IN_24_HOURS(40001, "Secret key can only be refreshed once in 24 hours"),
    ERROR_POOL_API_MALICIOUS_REFRESH_AK(40002, "Malicious refresh access token, the expire time of token is more than 30 minutes"),
    ERROR_POOL_API_WRONG_SECRET_KEY(40003, "Wrong sk"),
    ERROR_POOL_API_INVALID_ACCESS_KEY(40004, "Invalid ak"),
    ERROR_POOL_API_PROMOTION_IS_FROZEN(40005, "Promotion api is frozen"),
    ERROR_POOL_API_WRONG_BLOCKINDEX_QUERY_LEDGER(40006, "Wrong blockindex"),
    ERROR_POOL_API_BLOCKINDEX_IS_CURRENT(40007, "the blockindex is in production"),
    ERROR_POOL_API_BLOCK_NOT_BELONG_PROMOTION(40008, "the block is not belong the promotion"),
    ERROR_POOL_API_INVALID_PAGE_NUM_OR_SIZE(40009, "Invalid page num or size"),
    ERROR_POOL_API_THE_BLOCK_IS_IN_PRODUCT(40010, "the block is in producing"),
    ERROR_POOL_API_ACCESS_KEY_EXPIRED(40011, "The ak is expired"),
    ERROR_POOL_API_EMPTY_ACCESS_KEY(40012, "Empty ak"),

    // --- 7xxxx administrator ---
    ERROR_RULE_NOT_FOUND(70001, "Admin error:the biz rule not found"),


    // --- 88888
    ;


    private final int value;

    private final String reasonPhrase;

    HttpStatusExtend(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return this.value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    @Override
    public String toString() {
        return this.reasonPhrase;
    }

}
