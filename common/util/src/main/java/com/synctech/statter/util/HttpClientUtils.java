package com.statter.statter.util;

import com.alibaba.fastjson.JSONObject;
import com.statter.statter.constant.restful.AppBizException;
import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public class HttpClientUtils {

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).setConnectionRequestTimeout(60000).build();
    private static HttpClientBuilder BUILDER = HttpClientBuilder.create();

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
                throw new AppBizException(sl.getStatusCode(), sl.getReasonPhrase());
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
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static String post(HttpPost httpPost) {
        return post(httpPost, new BasicHttpContext());
    }

    public static String post(HttpPost httpPost, HttpContext context) {
        httpPost.setConfig(REQUEST_CONFIG);
        return doCloseable(httpPost, context);
    }

    public static String postJSON(String url, JSONObject raw) {
        HttpPost hp = new HttpPost(url);
        hp.addHeader("Content-Type", "application/json");
        try {
            hp.setEntity(new StringEntity(raw.toJSONString()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return HttpClientUtils.post(hp);
    }

    public static String get(HttpGet httpGet) {
        return get(httpGet, new BasicHttpContext());
    }

    public static String get(HttpGet httpGet, HttpContext context) {
        httpGet.setConfig(REQUEST_CONFIG);
        return doCloseable(httpGet, context);
    }

    public static String delete(HttpDelete httpDelete) {
        return delete(httpDelete, new BasicHttpContext());
    }

    public static String delete(HttpDelete httpDelete, HttpContext context) {
        httpDelete.setConfig(REQUEST_CONFIG);
        return doCloseable(httpDelete, context);
    }

    public static String put(HttpPut httpPut) {
        return put(httpPut, new BasicHttpContext());
    }

    public static String put(HttpPut httpPut, HttpContext context) {
        httpPut.setConfig(REQUEST_CONFIG);
        return doCloseable(httpPut, context);
    }

    public static String openText(String url) {

        return null;
    }


    public static HttpEntity genMultipartEntity(Map<String, Object> params) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName("utf-8"));
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (null != params) {
            Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = iterator.next();
                String k = entry.getKey();
                Object v = entry.getValue();
                if (v instanceof FileContainer) {
                    FileContainer file = (FileContainer) v;
                    builder.addBinaryBody(k, file.getInputStream(), ContentType.create("multipart/form-data"), file.getFileName());
                } else {
                    StringBody stringBody = null;
                    try {
                        stringBody = new StringBody(v.toString(), Charset.forName("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    builder.addPart(k, stringBody);
                }
            }
        }
        return builder.build();
    }

    @Data
    public static class FileContainer {

        InputStream inputStream;
        String fileName;

        public FileContainer(String fileName, InputStream inputStream) {
            this.fileName = fileName;
            this.inputStream = inputStream;
        }

    }


}
