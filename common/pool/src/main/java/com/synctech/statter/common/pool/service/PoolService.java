package com.synctech.statter.common.pool.service;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.common.pool.vo.Block;
import com.synctech.statter.common.pool.vo.MiningReportReq;
import com.synctech.statter.common.pool.vo.PoolTask;
import com.synctech.statter.constant.HttpStatusExtend;
import com.synctech.statter.constant.restful.AppBizException;
import com.synctech.statter.util.HttpClientUtils;
import com.synctech.statter.util.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipInputStream;

/**
 * gateway interface
 */
@Slf4j
@Service
public class PoolService {

    @Value("${statter.task.c-count:100}")
    Integer ccount;
    @Value("${statter.pool.host}")
    String poolHost;
    @Value("${statter.pool.port}")
    int poolPort;
    @Value("${statter.pool.get-block-index-url:}")
    String getBlockIndexUrl;
    @Value("${statter.pool.get-pool-task-url:}")
    String getPoolTaskUrl;
    @Value("${statter.pool.commit-pool-task-url:}")
    String commitPoolTaskUrl;
    @Value("${statter.pool.download-block-url:}")
    String downloadBlockUrl;

    public static void main(String[] args) throws FileNotFoundException {
        PoolService s = new PoolService();
        s.downloadBlockUrl = "http://<host>:<port>/tokdesktop/server/block.zip";
        s.poolHost = "34.134.98.47";
        s.poolPort = 80;
        long bi = 40;
        InputStream is = s.downloadBlock(bi);
        IoUtil.copy(is, new FileOutputStream("d:/tmp/blockdata." + bi + ".zip"));
    }

    private String getBlockIndexUrl() {
        ST st = new ST(getBlockIndexUrl);
        st.add("host", poolHost);
        st.add("port", poolPort);
        return st.render();
    }

    private String getPoolTaskUrl() {
        ST st = new ST(getPoolTaskUrl);
        st.add("host", poolHost);
        st.add("port", poolPort);
        return st.render();
    }

    private String commitPoolTaskUrl() {
        ST st = new ST(commitPoolTaskUrl);
        st.add("host", poolHost);
        st.add("port", poolPort);
        return st.render();
    }

    private String downloadBlockUrl() {
        ST st = new ST(this.downloadBlockUrl);
        st.add("host", poolHost);
        st.add("port", poolPort);
        return st.render();
    }

    /**
     * do ask block index operation
     *
     * @return
     */
    public long getBlockIndexImpl() {
        String url = this.getBlockIndexUrl();
        String resp = HttpClientUtils.post(new HttpPost(url));
        JSONObject r = JSONObject.parseObject(resp);
        if (!StringUtils.equals(r.getString("code"), "0")) {
            log.warn(r.getString("msg"));
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_GET_BLOCK_INDEX);
        }
        JSONObject jo = r.getJSONObject("content");
        if (!jo.containsKey("blockIndex")) {
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_GET_BLOCK_INDEX);
        }
        long bi = jo.getLongValue("blockIndex");
        return bi;
    }

    /**
     * do query mining task
     *
     * @param blockIndex
     * @param walletAddress
     * @return
     */
    public PoolTask getPoolTaskImpl(long blockIndex, String walletAddress, String hash) {
        String url = this.getPoolTaskUrl();
        JSONObject params = new JSONObject();
        params.put("walletAddress", walletAddress);
        params.put("blockIndex", blockIndex);
        params.put("machinesNum", hash);
        log.info("get pool task: {}", params.toJSONString());
        try {
            String resp = HttpClientUtils.postJSON(url, params);
            JSONObject r = JSONObject.parseObject(resp);
            if (!StringUtils.equals(r.getString("code"), "0")) {
                log.warn("error on ask pool task : {}", resp);
                throw new AppBizException(HttpStatusExtend.ERROR_POOL_GET_POOL_TASK);
            }
            PoolTask pt = r.getObject("content", PoolTask.class);
            pt.getBlock().setCCount(ccount);
            return pt;
        } catch (AppBizException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_GET_POOL_TASK);
        }
    }

    public void commitPoolTaskUrl(MiningReportReq req) {
        String url = this.commitPoolTaskUrl();
        try {
            log.info("commit compute result to pool: start = {}", JSONObject.toJSONString(req));
            String resp = HttpClientUtils.postJSON(url, JSONUtils.toJSONObject(req));
            JSONObject r = JSONObject.parseObject(resp);
            if (!StringUtils.equals(r.getString("code"), "0")) {
                log.warn("commit compute result to pool: error = {}", resp);
                throw new AppBizException(HttpStatusExtend.ERROR_POOL_COMMIT_RESULT);
            }
            log.info("commit compute result to pool: end = {}", r.toJSONString());
        } catch (AppBizException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_COMMIT_RESULT);
        }
    }

    public JSONArray downLoadBlockTradeFlow(long blockIndex) {
        JSONArray r = new JSONArray();
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(this.downloadBlock(blockIndex));
            ZipInputStream finalZis = zis;
            ZipUtil.read(zis, zipEntry -> {
                if (StringUtils.equals("contractblockfile", zipEntry.getName())) { // tradeflow
                    FastByteArrayOutputStream fbaos = IoUtil.read(finalZis, false);
                    String c = new String(fbaos.toByteArray());
                    fbaos.close();
                    if (!org.apache.commons.lang3.StringUtils.isBlank(c)) {
                        for (Object o : JSONArray.parseArray(c)) {
                            if (((JSONObject) o).size() > 0) r.add(o);
                        }
                    }
                }
            });
            return r;
        } finally {
            try {
                if (zis != null) zis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Block queryBlockLedgerId(long blockIndex) {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(this.downloadBlock(blockIndex));
            ZipInputStream finalZis = zis;
            AtomicReference<Block> b = new AtomicReference<>();
            ZipUtil.read(zis, zipEntry -> {
                if (StringUtils.equals("blockObject", zipEntry.getName())) { // block info
                    FastByteArrayOutputStream fbaos = IoUtil.read(finalZis, false);
                    String c = new String(fbaos.toByteArray());
                    fbaos.close();
                    if (!StringUtils.isBlank(c)) {
                        b.set(JSONObject.parseObject(c, Block.class));
                    }
                }
            });
            return b.get();
        } finally {
            try {
                if (zis != null) zis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private InputStream downloadBlock(long blockIndex) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(downloadBlockUrl());
            httpPost.setHeader("Content-type", "application/json; charset=utf-8");
            httpPost.setHeader("Connection", "Close");
            httpPost.addHeader("Accept-Encoding", "GZIP");
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(15000).setSocketTimeout(15000).build();
            httpPost.setConfig(requestConfig);
            StringEntity entity = new StringEntity(new NoticeParams(blockIndex + "", null, null).toJSONString(), Charset.forName("UTF-8"));
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            return response.getEntity().getContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_DOWNLOAD_BLOCK);
        } finally {
            try {
                if (httpPost != null) httpPost.releaseConnection();
                if (httpClient != null) httpClient.close();
                if (response != null) response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class NoticeParams implements Serializable {
        private String bn;

        private String ip;
        private String tradeName;

        public NoticeParams() {
        }

        public NoticeParams(String bn, String ip, String tradeName) {
            super();
            this.bn = bn;
            this.ip = ip;
            this.tradeName = tradeName;
        }

        public String getBn() {
            return bn;
        }

        public void setBn(String bn) {
            this.bn = bn;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String toJSONString() {
            return JSONObject.toJSONString(this);
        }

        public String getTradeName() {
            return tradeName;
        }

        public void setTradeName(String tradeName) {
            this.tradeName = tradeName;
        }

    }

}
