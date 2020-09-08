package com.alex.diytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.classloader.WebappClassLoader;
import com.alex.diytomcat.exception.WebConfigDuplicationException;
import com.alex.diytomcat.http.ApplicationContext;
import com.alex.diytomcat.util.ContextXMLUtil;
import com.alex.diytomcat.watcher.ContextFileChangeWatcher;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.*;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
@Log4j2
@ToString
public class Context {

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private String docBase;

    private File contextWebXmlFile;

    @Getter
    private WebappClassLoader webappClassLoader;

    @Getter
    @Setter
    private Host host;

    @Getter
    @Setter
    private boolean reloadable;

    @Getter
    @Setter
    private ContextFileChangeWatcher contextFileChangeWatcher;

    @Getter
    private ServletContext servletContext;

    private Map<String, String> url_servletClassName;
    private Map<String, String> url_servletName;
    private Map<String, String> servletName_className;
    private Map<String, String> className_servletName;

    public Context(String path, String docBase, Host host, boolean reloadable) {
        TimeInterval timeInterval = DateUtil.timer();
        this.path = path;
        this.docBase = docBase;
        this.contextWebXmlFile = new File(docBase, ContextXMLUtil.getWatchedResource());
        this.url_servletClassName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();
        ClassLoader commonClassLoader = Thread.currentThread().getContextClassLoader();
        this.webappClassLoader = new WebappClassLoader(docBase, commonClassLoader);
        this.host = host;
        this.reloadable = reloadable;
        this.servletContext = new ApplicationContext(this);

        deploy();
    }

    public String getServletClassName(String uri) {
        return url_servletClassName.get(uri);
    }

    private void deploy() {
        TimeInterval timeInterval = DateUtil.timer();
        log.info("Deploying web application directory {} for the path {}", this.docBase, this.path);
        init();
        if (reloadable) {
            contextFileChangeWatcher = new ContextFileChangeWatcher(this);
            contextFileChangeWatcher.start();
        }
        log.info("Deployment of web application directory {} finished in {} ms", this.getDocBase(), timeInterval.intervalMs());
    }

    public void reload() {
        host.reload(this);
    }

    public void stop() {
        webappClassLoader.stop();
        contextFileChangeWatcher.stop();
    }

    private void init() {
        if (!contextWebXmlFile.exists()) {
            return;
        }

        try {
            checkDuplication();
        } catch (WebConfigDuplicationException e) {
            e.printStackTrace();
            return;
        }

        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);
        parseServletMapping(d);
    }

    private void parseServletMapping(Document d) {
        // url_ServletName
        Elements mappingurlElements = d.select("servlet-mapping url-pattern");
        for (Element mappingurlElement : mappingurlElements) {
            String urlPattern = mappingurlElement.text();
            String servletName = mappingurlElement.parent().select("servlet-name").first().text();
            url_servletName.put(urlPattern, servletName);
        }

        // servletName_className / className_servletName
        Elements servletNameElements = d.select("servlet servlet-name");
        for (Element servletNameElement : servletNameElements) {
            String servletName = servletNameElement.text();
            String servletClass = servletNameElement.parent().select("servlet-class").first().text();
            servletName_className.put(servletName, servletClass);
            className_servletName.put(servletClass, servletName);
        }

        // url_servletClassName
        Set<String> urls = url_servletName.keySet();
        for (String url : urls) {
            String servletName = url_servletName.get(url);
            String servletClassName = servletName_className.get(servletName);
            url_servletClassName.put(url, servletClassName);
        }
    }

    private void checkDuplication() throws WebConfigDuplicationException {
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);

        checkDuplication(d, "servlet-mapping url-pattern", "servlet url is duplicated:{} ");
        checkDuplication(d, "servlet servlet-name", "servlet name is duplicated:{} ");
        checkDuplication(d, "servlet servlet-class", "servlet class name is duplicated:{} ");
    }

    private void checkDuplication(Document d, String mapping, String desc) throws WebConfigDuplicationException {
        Elements elements = d.select(mapping);
        List<String> contents = new ArrayList<>();
        for (Element e : elements) {
            contents.add(e.text());
        }

        Collections.sort(contents);

        for (int i = 0; i < contents.size() - 1; i++) {
            String contentPre = contents.get(i);
            String contentNext = contents.get(i + 1);
            if (contentPre.equals(contentNext)) {
                throw new WebConfigDuplicationException(StrUtil.format(desc, contentPre));
            }
        }
    }
}
