package com.alex.diytomcat.util;

import cn.hutool.core.io.FileUtil;
import com.alex.diytomcat.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
public class WebXmlUtil {

    private static Map<String, String> mimeTypeMapping = new HashMap<>();

    public static String getWelcomeFile(final Context context) {
        String xml = FileUtil.readUtf8String(Constants.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements elements = d.select("welcome-file");
        for (Element e : elements) {
            String welcomeFileName = e.text();
            File f = new File(context.getDocBase(), welcomeFileName);
            if (f.exists()) {
                return f.getName();
            }
        }

        return "index.html";
    }

    public static synchronized String getMimeType(String extension) {
        if (mimeTypeMapping.isEmpty()) {
            initMimeType();
        }
        String mimeType = mimeTypeMapping.get(extension);
        if (null == mimeType) {
            return "text/html";
        }
        return mimeType;
    }

    private static void initMimeType() {
        String xml = FileUtil.readUtf8String(Constants.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements elements = d.select("mime-mapping");
        for (Element e : elements) {
            String extension = e.select("extension").first().text();
            String mimeType = e.select("mime-type").first().text();
            mimeTypeMapping.put(extension, mimeType);
        }
    }
}
