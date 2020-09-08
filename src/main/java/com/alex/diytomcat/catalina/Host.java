package com.alex.diytomcat.catalina;

import com.alex.diytomcat.util.Constants;
import com.alex.diytomcat.util.ServerXMLUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Host <==> A website, which can have multiple context
 *
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
@Slf4j
public class Host {

    @Getter
    private Engine engine;

    @Getter
    @Setter
    private String name;

    @Getter
    private Map<String, Context> contextMap;

    public Host(String name, Engine engine) {
        this.engine = engine;
        this.contextMap = new HashMap<>();
        this.name = name;

        /*
         * Load all contexts under webapp folder
         */
        scanContextsOnWebAppsFolder();
        /*
         * Load all contexts in conf/server.xml
         */
        scanContextsInServerXML();
    }

    public Context getContext(String path) {
        return contextMap.get(path);
    }

    private void scanContextsInServerXML() {
        List<Context> contexts = ServerXMLUtil.getContexts(this);
        for (Context context : contexts) {
            contextMap.put(context.getPath(), context);
        }
    }

    private void scanContextsOnWebAppsFolder() {
        File[] folders = Constants.webappsFolder.listFiles();
        for (File folder : folders) {
            if (!folder.isDirectory()) {
                continue;
            }
            loadContext(folder);
        }
    }

    private void loadContext(File folder) {
        String path = folder.getName();
        if ("ROOT".equals(path)) {
            path = "/";
        } else {
            path = "/" + path;
        }

        String docBase = folder.getAbsolutePath();
        Context context = new Context(path, docBase, this, true);
        contextMap.put(context.getPath(), context);
    }

    public void reload(Context context) {
        log.info("Reloading Context with name [{}] has started", context.getPath());
        String path = context.getPath();
        String docBase = context.getDocBase();
        boolean reloadable = context.isReloadable();

        // stop
        context.stop();
        // remove
        contextMap.remove(path);
        // new context
        Context newContext = new Context(path, docBase, this, reloadable);
        // put into map
        contextMap.put(newContext.getPath(), newContext);
        log.info("Reloading Context with name [{}] has completed", context.getPath());
    }
}
