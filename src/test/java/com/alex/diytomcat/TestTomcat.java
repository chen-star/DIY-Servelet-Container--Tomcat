package com.alex.diytomcat;

import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTomcat {
    private static int port = 18080;
    private static String ip = "127.0.0.1";

    @BeforeClass
    public static void beforeClass() {
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
        Assert.assertTrue(html.contains("Hello From Alex's DIY Tomcat -- [HTML]"));
    }

    private String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;
    }
}