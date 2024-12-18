package com.statter.statter.common.pool.service;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.statter.statter.common.pool.vo.Block;
import com.statter.statter.common.pool.vo.MiningReportReq;
import com.statter.statter.common.pool.vo.PoolTask;
import com.statter.statter.constant.HttpStatusExtend;
import com.statter.statter.constant.restful.AppBizException;
import com.statter.statter.util.HttpClientUtils;
import com.statter.statter.util.JSONUtils;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * gateway interface
 */
@Slf4j
@Service
public class PoolService {

    @Value("${statter.task.c-count:1}")
    Integer ccount;
    @Value("${statter.pool.host}")
    String poolHost = "10.128.0.4";
    @Value("${statter.pool.port}")
    int poolPort = 9010;
    @Value("${statter.pool.port}")
    int poolPortTokdesktop = 8083;
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
        s.poolPortTokdesktop = 80;
        long bi = 791583;
        InputStream is = s.downloadBlock(bi);

        Block b = s.queryBlockLedgerId(bi);
        System.out.printf("block: %s", JSONObject.toJSONString(b));
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
        st.add("port", poolPortTokdesktop);
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
        //log.info("get pool task [url = {}] : {}", url, params.toJSONString());
        try {
            long st = System.currentTimeMillis();
            String resp = HttpClientUtils.postJSON(url, params);
            JSONObject r = JSONObject.parseObject(resp);
//            log.info("ask pool task response [code = {}] [cost time = {}] : \n{}\n{}",
//                    r.getString("code"),
//                    System.currentTimeMillis() - st,
//                    params.toJSONString(),
//                    resp
//            );
            if (!StringUtils.equals(r.getString("code"), "0")) {
                throw new AppBizException(HttpStatusExtend.ERROR_POOL_GET_POOL_TASK);
            }
            PoolTask pt = r.getObject("content", PoolTask.class);
            pt.getBlock().setCCount(ccount);
            log.info("get pool task success  [url = {}]: {}", url, pt.toString());
            return pt;
        } catch (AppBizException e) {
            //log.error("get pool task error [url = {}]: {}", url, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_GET_POOL_TASK);
        }
    }

    public void commitPoolTaskUrl(MiningReportReq req) {
        String url = this.commitPoolTaskUrl();
        try {
            log.info("commit compute result to pool[start(148)]: {}", JSONObject.toJSONString(req));
            String resp = HttpClientUtils.postJSON(url, JSONUtils.toJSONObject(req));
            JSONObject r = JSONObject.parseObject(resp);
            if (!StringUtils.equals(r.getString("code"), "0")) {
                if (!StringUtils.equals(r.getString("code"), "-1"))
                    throw new AppBizException(HttpStatusExtend.ERROR_POOL_COMMIT_RESULT_EXPIRE_BLOCKINDEX);
                log.warn("commit compute result response error[code={}]: {}", r.getString("code"), resp);
                throw new AppBizException(HttpStatusExtend.ERROR_POOL_COMMIT_RESULT);
            }
            log.info("commit compute result to pool[success(155)]: {}", r.toJSONString());
        } catch (AppBizException e) {
            throw e;
        } catch (Exception e) {
            log.error("commit compute result to pool[commitPoolTaskUrl(159)]: {}", e.getMessage());
            throw new AppBizException(HttpStatusExtend.ERROR_POOL_COMMIT_RESULT);
        }
    }

    public JSONArray downLoadBlockTradeFlow(long blockIndex) {
        JSONArray r = new JSONArray();
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(this.downloadBlock(blockIndex), Charset.forName("UTF-8"));
            ZipEntry ze = null;
            while ((ze = zis.getNextEntry()) != null) {
                if (StringUtils.equals("contractblockfile", ze.getName())) { // tradeflow
                    FastByteArrayOutputStream fbaos = IoUtil.read(zis);
                    String c = new String(fbaos.toByteArray());
                    fbaos.close();
                    if (!org.apache.commons.lang3.StringUtils.isBlank(c)) {
                        for (Object o : JSONArray.parseArray(c)) {
                            if (((JSONObject) o).size() > 0) r.add(o);
                        }
                    }
                    break;
                }
            }
            /*ZipInputStream finalZis = zis;
            ZipUtil.read(zis, zipEntry -> {
                if (StringUtils.equals("contractblockfile", zipEntry.getName())) { // tradeflow
                    FastByteArrayOutputStream fbaos = IoUtil.read(finalZis);
                    String c = new String(fbaos.toByteArray());
                    fbaos.close();
                    if (!org.apache.commons.lang3.StringUtils.isBlank(c)) {
                        for (Object o : JSONArray.parseArray(c)) {
                            if (((JSONObject) o).size() > 0) r.add(o);
                        }
                    }
                }
            });*/
            return r;
        } catch (IOException e) {
            log.error("error occur when analyze contract block file:", e);
            throw new RuntimeException(e);
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
            zis = new ZipInputStream(this.downloadBlock(blockIndex), Charset.forName("UTF-8"));
            AtomicReference<Block> b = new AtomicReference<>();
            ZipEntry ze = null;
            while ((ze = zis.getNextEntry()) != null) {
                if (StringUtils.equals("blockObject", ze.getName())) { // block info
                    FastByteArrayOutputStream fbaos = IoUtil.read(zis);
                    String c = new String(fbaos.toByteArray());
                    fbaos.close();
                    if (!StringUtils.isBlank(c)) {
                        b.set(JSONObject.parseObject(c, Block.class));
                    }
                    break;
                }
            }
            /*ZipInputStream finalZis = zis;
            ZipUtil.read(zis, zipEntry -> {
                if (StringUtils.equals("blockObject", zipEntry.getName())) { // block info
                    FastByteArrayOutputStream fbaos = IoUtil.read(finalZis);
                    String c = new String(fbaos.toByteArray());
                    fbaos.close();
                    if (!StringUtils.isBlank(c)) {
                        b.set(JSONObject.parseObject(c, Block.class));
                    }
                }
            });*/
            return b.get();
        } catch (IOException e) {
            log.error("error occur when analyze block object:", e);
            throw new RuntimeException(e);
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
            InputStream is = response.getEntity().getContent();
            byte[] d = IoUtil.readBytes(is);
            ByteArrayInputStream bis = IoUtil.toStream(d);
            return bis;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
