package com.synctech.statter.mining.pool.api.utils;

import cn.hutool.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class StatterMiningPoolAccessKeyUtils {

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).setConnectionRequestTimeout(60000).build();
    private static HttpClientBuilder BUILDER = HttpClientBuilder.create();

    private static String genSecretKeyUri = "/statter/mining/pool/api/v1/sk/refresh", getAccessKeyUri = "/statter/mining/pool/api/v1/ak/refresh";



    public static JSONObject getAccessKey(String statterApiServerHost, int statterApiServerPort, String promotionAddress, String secretKey) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://").append(statterApiServerHost).append(":").append(statterApiServerPort).append(getAccessKeyUri);
        HttpPost hp = new HttpPost(urlBuilder.toString());
        hp.addHeader("Content-Type", "application/json");
        hp.setEntity(new StringEntity(new JSONObject()
                .set("a", promotionAddress)
                .set("sk", secretKey)
                .toString()));
        String s = doCloseable(hp, new BasicHttpContext());
        JSONObject r = new JSONObject(s);
        if (r.getInt("code") != 0) throw new RuntimeException(r.getStr("message"));
        return r.getJSONObject("data");
    }

    public static String genSecretKey(String statterApiServerHost, int statterApiServerPort, String promotionAddress, String promotionManageKey) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("http://").append(statterApiServerHost).append(":").append(statterApiServerPort).append(genSecretKeyUri);
        HttpPost hp = new HttpPost(urlBuilder.toString());
        hp.addHeader("Content-Type", "application/json");
        hp.setEntity(new StringEntity(new JSONObject()
                .set("a", promotionAddress)
                .set("mk", promotionManageKey)
                .toString()));
        String s = doCloseable(hp, new BasicHttpContext());
        JSONObject r = new JSONObject(s);
        if (r.getInt("code") != 0) throw new RuntimeException(r.getStr("message"));
        return r.getStr("data");
    }

    private static String doCloseable(HttpUriRequest request, HttpContext context) {
        CloseableHttpClient client = BUILDER.build();
        CloseableHttpResponse resp = null;
        try {
            resp = client.execute(request, context);
            StatusLine sl = resp.getStatusLine();
            if (null == sl) {
                throw new RuntimeException("System error, unable to access the target");
            } else if (302 == sl.getStatusCode()) {
                Header header = resp.getFirstHeader("location");
                String redirectUrl = header.getValue();
                HttpPost redirectHttp = new HttpPost(redirectUrl);
                redirectHttp.setConfig(REQUEST_CONFIG);
                redirectHttp.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
                redirectHttp.setEntity(resp.getEntity());
                resp = client.execute(redirectHttp);
            } else if (200 != sl.getStatusCode()) {
                throw new RuntimeException(sl.getReasonPhrase());
            }
            String result = EntityUtils.toString(resp.getEntity(), "utf-8");
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != resp) {
                try {
                    resp.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
