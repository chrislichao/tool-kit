package org.chrisli.utils.web;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [Http工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * [Response输出信息]
     */
    public static void responseWriteData(HttpServletResponse response, String value) {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 获得输出流
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            // 将数据写入输出流
            pw.write(value != null ? value : "null");
            // 清除数据流缓冲区
            pw.flush();
        } catch (Exception e) {
            logger.error("Response输出流异常!", e);
        } finally {
            if (pw != null) {
                // 关闭输出流
                pw.close();
            }
        }
    }

    /**
     * [从url中获取主机名]
     */
    public static String getHostFromURL(String url) {
        String host = "";
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }

    /**
     * [Http Get请求]
     */
    public static String httpFormUrlEncodedGet(String url) {
        return httpGet(url, ContentType.APPLICATION_FORM_URLENCODED, null);
    }

    /**
     * [Http Get请求]
     */
    private static String httpGet(String url, ContentType contentType, Map<String, String> headerMap) {
        // 创建HttpGet
        String result = null;
        HttpClient httpClient = getHttpClient(url.startsWith("https:"), getHostFromURL(url));
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", contentType + ";charset=utf-8");
            if (headerMap != null && CollectionUtils.isNotEmpty(headerMap.keySet())) {
                for (String key : headerMap.keySet()) {
                    httpGet.setHeader(key, headerMap.get(key));
                }
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
            httpGet.setConfig(requestConfig);
            // 执行客户端请求
            HttpEntity entity = httpClient.execute(httpGet).getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
                EntityUtils.consume(entity);
            }

        } catch (Throwable e) {
            logger.error("【HTTP GET请求失败】: url={}", url);
        }

        return result;
    }

    /**
     * [发送contentType=x-www-form-urlencoded的POST请求]
     */
    public static String httpFormUrlEncodedPost(String url, String content, boolean needSSL) {
        return httpPost(url, ContentType.APPLICATION_FORM_URLENCODED, null, content, needSSL);
    }

    /**
     * [发送contentType=json的POST请求]
     */
    public static String httpJsonPost(String url, String content, boolean needSSL) {
        return httpPost(url, ContentType.APPLICATION_JSON, null, content, needSSL);
    }

    /**
     * [发送contentType=json的POST请求,并额外添加请求头信息]
     */
    public static String httpJsonPost(String url, Map<String, String> headerMap, String content, boolean needSSL) {
        return httpPost(url, ContentType.APPLICATION_JSON, headerMap, content, needSSL);
    }

    /**
     * [Http Post请求]
     */
    private static String httpPost(String url, ContentType contentType, Map<String, String> headerMap, String content, boolean needSSL) {
        // 创建HttpPost
        String result = null;
        HttpClient httpClient = getHttpClient(needSSL, getHostFromURL(url));
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", contentType + ";charset=utf-8");
            if (headerMap != null && CollectionUtils.isNotEmpty(headerMap.keySet())) {
                for (String key : headerMap.keySet()) {
                    httpPost.setHeader(key, headerMap.get(key));
                }
            }
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
            httpPost.setConfig(requestConfig);
            BasicHttpEntity requestBody = new BasicHttpEntity();
            requestBody.setContent(new ByteArrayInputStream(content.getBytes("utf-8")));
            requestBody.setContentLength(content.getBytes("utf-8").length);
            httpPost.setEntity(requestBody);
            // 执行客户端请求
            HttpEntity entity = httpClient.execute(httpPost).getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity, "utf-8");
                EntityUtils.consume(entity);
            }

        } catch (Throwable e) {
            logger.error("【HTTP POST请求失败】: url={}, content={}", url, content);
        }

        return result;
    }

    /**
     * [获取HttpClient]
     */
    private static DefaultHttpClient getHttpClient(boolean sslClient, String host) {
        DefaultHttpClient httpclient = null;
        if (sslClient) {
            try {
                SSLHttpClient chc = new SSLHttpClient();
                InetAddress address = null;
                String ip;
                try {
                    address = InetAddress.getByName(host);
                    ip = address.getHostAddress().toString();
                    httpclient = chc.registerSSL(ip, "TLS", 443, "https");
                } catch (UnknownHostException e) {
                    logger.error("获取请求服务器地址失败：host = {} " + host);
                    e.getStackTrace().toString();
                }
                HttpParams hParams = new BasicHttpParams();
                hParams.setParameter("https.protocols", "SSLv3,SSLv2Hello");
                httpclient.setParams(hParams);
            } catch (KeyManagementException e) {
                logger.error(e.getStackTrace().toString());
            } catch (NoSuchAlgorithmException e) {
                logger.error(e.getStackTrace().toString());
            }
        } else {
            httpclient = new DefaultHttpClient();
        }
        return httpclient;
    }
}
