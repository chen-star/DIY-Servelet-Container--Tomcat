package com.alex.diytomcat.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import com.alex.diytomcat.catalina.*;
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

    public static List<Context> getContexts(Host host) {
        List<Context> contexts = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constants.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Elements elements = d.select("Context");
        for (Element element : elements) {
            String path = element.attr("path");
            String docBase = element.attr("docBase");
            boolean reloadable = Convert.toBool(element.attr("reloadable"), true);
            Context context = new Context(path, docBase, host, reloadable);
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

    public static String getServiceName() {
        String xml = FileUtil.readUtf8String(Constants.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Element host = d.selectFirst("Service");
        return host.attr("name");
    }

    public static List<Connector> getConnectors(Service service) {
        List<Connector> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constants.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("Connector");
        for (Element e : es) {
            int port = Convert.toInt(e.attr("port"));
            String compression = e.attr("compression");
            int compressionMinSize = Convert.toInt(e.attr("compressionMinSize"), 0);
            String noCompressionUserAgents = e.attr("noCompressionUserAgents");
            String compressableMimeType = e.attr("compressableMimeType");
            Connector c = new Connector(service);
            c.setPort(port);
            c.setCompression(compression);
            c.setCompressableMimeType(compressableMimeType);
            c.setNoCompressionUserAgents(noCompressionUserAgents);
            c.setCompressableMimeType(compressableMimeType);
            c.setCompressionMinSize(compressionMinSize);
            result.add(c);
        }
        return result;
    }
}
