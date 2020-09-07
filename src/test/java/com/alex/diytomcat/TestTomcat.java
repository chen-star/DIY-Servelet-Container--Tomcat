package com.alex.diytomcat;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        String response  = getHeaderString("/not_exist.html");
        System.out.println(response);
        Assert.assertTrue(response.contains("HTTP/1.1 404 Not Found"));
    }

    @Test
    public void test500() {
        String response  = getHeaderString("/500.html");
        System.out.println(response);
        Assert.assertTrue(response.contains("HTTP/1.1 500 Internal Server Error"));
    }

    private String getHeaderString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip,port,uri);
        String http = MiniBrowser.getHttpString(url);
        return http;
    }

    private String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }
}