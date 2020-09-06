package com.alex.diytomcat.util;

import cn.hutool.core.io.FileUtil;
import com.alex.diytomcat.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
public class ServerXMLUtil {

    public static List<Context> getContexts() {
        List<Context> contexts = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constants.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Elements elements = d.select("Context");
        for (Element element : elements) {
            String path = element.attr("path");
            String docBase = element.attr("docBase");
            Context context = new Context(path, docBase);
            contexts.add(context);
        }

        return contexts;
    }
}
