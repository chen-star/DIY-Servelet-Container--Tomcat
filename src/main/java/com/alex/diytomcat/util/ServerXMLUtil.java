package com.alex.diytomcat.util;

import cn.hutool.core.io.FileUtil;
import com.alex.diytomcat.catalina.Context;
import com.alex.diytomcat.catalina.Engine;
import com.alex.diytomcat.catalina.Host;
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

    public static String getHostName() {
        String xml = FileUtil.readUtf8String(Constants.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Element host = d.selectFirst("Host");
        return host.attr("name");
    }

    public static String getEngineDefaultHost() {
        String xml = FileUtil.readUtf8String(Constants.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Element host = d.selectFirst("Engine");
        return host.attr("defaultHost");
    }

    public static List<Host> getHosts(Engine engine) {
        List<Host> hosts = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constants.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements elements = d.select("Host");
        for (Element e : elements) {
            String name = e.attr("name");
            Host host = new Host(name, engine);
            hosts.add(host);
        }
        return hosts;
    }
}
