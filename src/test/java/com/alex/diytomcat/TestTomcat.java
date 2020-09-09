package com.alex.diytomcat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import com.alex.diytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.alex.diytomcat.util.MiniBrowser.getHttpString;

public class TestTomcat {
    private static final int port = 18080;
    private static final String ip = "127.0.0.1";

    @BeforeClass
    public static void beforeClass() {
        // Pre check if tomcat has already up and running
        if (NetUtil.isUsableLocalPort(port)) {
            System.err.println("Please start up tomcat at port: " + port);
            System.exit(1);
        } else {
            System.out.println("----- Test Begin -----");
        }
    }

    @Test
    public void testHelloTomcat() {
        String html = getContentString("/");
        System.out.println(html);
        Assert.assertTrue(html.contains("Hello From Alex's DIY Tomcat"));
    }

    @Test
    public void testaHtml() {
        String html = getContentString("/a.html");
        System.out.println(html);
        Assert.assertTrue(html.contains("Hello From Alex's DIY Tomcat -- [a.html]"));
    }

    @Test
    public void testTimeConsumeHtml() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10));
        TimeInterval timeInterval = DateUtil.timer();

        for (int i = 0; i < 3; i++) {
            threadPool.execute(new Runnable() {
                public void run() {
                    getContentString("/timeConsume.html");
                }
            });
        }
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.HOURS);

        long duration = timeInterval.intervalMs();

        Assert.assertTrue(duration < 3000);
    }

    @Test
    public void testNotRootHtml() {
        String html = getContentString("/a/index.html");
        System.out.println(html);
        Assert.assertTrue(html.contains("Hello From Alex's DIY Tomcat -- [a/index.html]"));
    }

    @Test
    public void testConfHtml() {
        String html = getContentString("/b/index.html");
        System.out.println(html);
        Assert.assertTrue(html.contains("Hello From Alex's DIY Tomcat -- [b/index.html]"));
    }

    @Test
    public void test404() {
        String response = getHeaderString("/not_exist.html");
        System.out.println(response);
        Assert.assertTrue(response.contains("HTTP/1.1 404 Not Found"));
    }

    @Test
    public void test500() {
        String response = getHeaderString("/500.html");
        System.out.println(response);
        Assert.assertTrue(response.contains("HTTP/1.1 500 Internal Server Error"));
    }

    @Test
    public void testNotDefaultWelcomeHtml() {
        String html = getContentString("/a");
        System.out.println(html);
        Assert.assertTrue(html.contains("Hello From Alex's DIY Tomcat -- [a/index.html]"));
    }

    @Test
    public void testaTxt() {
        String response = getHeaderString("/a/a.txt");
        System.out.println(response);
        Assert.assertTrue(response.contains("Content-Type: text/plain"));
    }

    @Test
    public void testJpg() {
        byte[] bytes = getContentBytes("/car.png");
        int pngFileLength = 959;
        Assert.assertEquals(pngFileLength, bytes.length);
    }

    @Test
    public void testPDF() {
        String uri = "/sample.pdf";
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HttpUtil.download(url, baos, true);
        int pdfFileLength = 3028;
        Assert.assertEquals(pdfFileLength, baos.toByteArray().length);
    }

    @Test
    public void testHelloServlet() {
        String html = getContentString("/j2ee/hello");
        System.out.println(html);
        Assert.assertTrue(html.contains("Hello Alex's Tomcat from HelloServlet"));
    }

    @Test
    public void testJavaWebHello() {
        String html = getContentString("/javaweb/hello");
        System.out.println(html);
        Assert.assertTrue(html.contains("Hello DIY Tomcat from HelloServlet@javaweb"));
    }

    @Test
    public void testServletSingleton() {
        String html1 = getContentString("/javaweb/hello");
        String html2 = getContentString("/javaweb/hello");
        Assert.assertEquals(html1, html2);
    }

    @Test
    public void testGetParam() {
        String uri = "/javaweb/param";
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Alex Chen");
        String html = MiniBrowser.getContentString(url, params, true);
        System.out.println(html);
        Assert.assertEquals(html, "get name:Alex Chen");
    }

    @Test
    public void testPostParam() {
        String uri = "/javaweb/param";
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        Map<String, Object> params = new HashMap<>();
        params.put("name", "Alex Chen");
        String html = MiniBrowser.getContentString(url, params, false);
        Assert.assertEquals(html, "post name:Alex Chen");
    }

    @Test
    public void testHeader() {
        String html = getContentString("/javaweb/header");
        System.out.println(html);
        Assert.assertEquals(html, "Alex browser Agent");
    }

    @Test
    public void testSetCookie() {
        String uri = "/javaweb/setCookie";
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String html = getHttpString(url);
        System.out.println(html);
        Assert.assertTrue(html.contains("set cookie successfully! name:Alex(cookie)"));
    }

    @Test
    public void testGetCookie() throws IOException {
        String url = StrUtil.format("http://{}:{}{}", ip, port, "/javaweb/getCookie");
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestProperty("Cookie", "name=Alex(cookie)");
        conn.connect();
        InputStream is = conn.getInputStream();
        String html = IoUtil.read(is, "utf-8");
        System.out.println(html);
        Assert.assertTrue(html.contains("name:Alex(cookie)"));
    }

    @Test
    public void testSession() throws IOException {
        String jsessionid = getContentString("/javaweb/setSession");
        if (null != jsessionid)
            jsessionid = jsessionid.trim();
        String url = StrUtil.format("http://{}:{}{}", ip, port, "/javaweb/getSession");
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setRequestProperty("Cookie", "JSESSIONID=" + jsessionid);
        conn.connect();
        InputStream is = conn.getInputStream();
        String html = IoUtil.read(is, "utf-8");
        System.out.println(html);
        Assert.assertTrue(html.contains("Alex(session)"));
    }

    @Test
    public void testGzip() {
        byte[] gzipContent = getContentBytes("/", true);
        byte[] unGzipContent = ZipUtil.unGzip(gzipContent);
        String html = new String(unGzipContent);
        System.out.println(html);
        Assert.assertTrue(html.contains("Hello From Alex's DIY Tomcat"));
    }

    @Test
    public void testClientJump() {
        String url = StrUtil.format("http://{}:{}{}", ip, port, "/javaweb/jump1");
        String http_servlet = getHttpString(url);
        System.out.println(http_servlet);
        Assert.assertTrue(http_servlet.contains("HTTP/1.1 302 Found"));
    }

    public void testJsp() {
        String html = getContentString("/javaweb/");
        System.out.println(html);
        Assert.assertEquals(html, "hello jsp@javaweb");
    }

    private byte[] getContentBytes(String uri) {
        return getContentBytes(uri, false);
    }

    private byte[] getContentBytes(String uri, boolean gzip) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        return MiniBrowser.getContentBytes(url, gzip);
    }

    private String getHeaderString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String http = getHttpString(url);
        return http;
    }

    private String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }
}