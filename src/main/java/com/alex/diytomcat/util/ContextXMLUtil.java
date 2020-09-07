package com.alex.diytomcat.util;

import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
public class ContextXMLUtil {

    public static String getWatchedResource() {
        try {
            String xml = FileUtil.readUtf8String(Constants.contextXmlFile);
            Document d = Jsoup.parse(xml);
            Element e = d.selectFirst("WatchedResource");
            return e.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "WEB-INF/web.xml";
        }
    }
}
