package com.wt.complaint.manage.infrastructure.utils;

import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;
import com.wt.complaint.manage.domain.exception.BusinessException;
import static com.wt.complaint.manage.domain.exception.ErrorCodeEnums.THIRD_SERVICE_ERROR;
import com.wt.nr.common.utils.GsonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 来自于soc工程,用于飞书相关接口调用
 */
@Slf4j
public class HttpClientV4 {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String JSON_TYPE = "application/json;charset=UTF-8";
    public static final String UTF_8 = "UTF-8";
    public static final String CONNECTION = "Connection";
    public static final String CLOSE = "close";

    private HttpClientV4() {
    }

    public static String postOrPutWithTimeout(String url, String body, Map<String, String> headers, Integer timeout,
                                              String method) {
        long start = System.currentTimeMillis();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            // method �?POST 或�?PUT
            conn.setRequestMethod(method);
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            }
            conn.addRequestProperty(CONNECTION, CLOSE);
            conn.getOutputStream().write(body.getBytes());
            // 加判断， 失败不要抛异常，拿出失败原因，兼容飞�?00场景
            return getResult(conn).content;
        } catch (Exception ex) {
            log.error("HttpClientV4#postOrPutWithTimeout post fail, url={}, body={} headers={}", url, body,
                    GsonUtil.toJson(headers), ex);
            throw new BusinessException(THIRD_SERVICE_ERROR, ex.getMessage());
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
            long end = System.currentTimeMillis();
            long cost = end - start;
            log.info("HttpClientV4#postOrPutWithTimeout cost:{}, url:{}", cost, url);
        }
    }

    public static String post(String url, String body, Map<String, String> headers) {
        // 默认http接口5s超市
        return postOrPutWithTimeout(url, body, headers, 5000, "POST");
    }

    public static HttpResult getResult(HttpURLConnection conn) throws IOException {
        int respCode = conn.getResponseCode();

        InputStream inputStream;
        if (HttpURLConnection.HTTP_OK == respCode || HttpURLConnection.HTTP_NOT_MODIFIED == respCode) {
            inputStream = conn.getInputStream();
        } else {
            inputStream = conn.getErrorStream();
        }

        Map<String, String> respHeaders = new HashMap<>(conn.getHeaderFields().size());
        for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
            respHeaders.put(entry.getKey(), entry.getValue().get(0));
        }

        String encodingGzip = "gzip";

        if (encodingGzip.equals(respHeaders.get(HttpHeaders.CONTENT_ENCODING))) {
            inputStream = new GZIPInputStream(inputStream);
        }

        return new HttpResult(respCode, new String(ByteStreams.toByteArray(inputStream), getCharset(conn)),
                respHeaders);
    }

    public static String getCharset(HttpURLConnection conn) {
        String contentType = conn.getContentType();
        if (null == contentType || contentType.equals("")) {
            return UTF_8;
        }

        String[] values = contentType.split(";");
        if (values.length == 0) {
            return UTF_8;
        }

        String charset = UTF_8;
        for (String value : values) {
            value = value.trim();

            if (value.toLowerCase().startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }

        return charset;
    }

    public static void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext(); ) {
                conn.addRequestProperty(iter.next(), iter.next());
            }
        }
        conn.addRequestProperty("Accept-Charset", encoding);
    }

    public static String encodingParams(Multimap<String, String> params, String encoding)
            throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == params || params.isEmpty()) {
            return null;
        }

        params.put("encoding", encoding);

        for (Map.Entry<String, String> entry : params.entries()) {
            if (null == entry.getValue() || entry.getValue().equals("")) {
                continue;
            }

            sb.append(entry.getKey()).append("=");
            sb.append(URLEncoder.encode(entry.getValue(), encoding));
            sb.append("&");
        }

        return sb.toString();
    }

    public static class HttpResult {

        @Getter
        private final int code;

        private final String content;

        private final Map<String, String> respHeaders;

        public HttpResult(int code, String content, Map<String, String> respHeaders) {
            this.code = code;
            this.content = content;
            this.respHeaders = respHeaders;
        }

        public String getHeader(String name) {
            return respHeaders.get(name);
        }

    }
}