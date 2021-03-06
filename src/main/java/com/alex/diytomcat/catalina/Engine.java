package com.alex.diytomcat.catalina;

import com.alex.diytomcat.util.ServerXMLUtil;
import lombok.Getter;

import java.util.List;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
public class Engine {

    private String defaultHost = ServerXMLUtil.getEngineDefaultHost();

    private List<Host> hosts;

    @Getter
    private Service service;

    public Engine(Service service) {
        this.service = service;
        this.defaultHost = ServerXMLUtil.getEngineDefaultHost();
        this.hosts = ServerXMLUtil.getHosts(this);
        checkDefault();
    }

    private void checkDefault() {
        if (getDefaultHost() == null) {
            throw new RuntimeException("DefaultHost=" + defaultHost + "doesn't exist!");
        }
    }

    public Host getDefaultHost() {
        for (Host host : hosts) {
            if (host.getName().equals(defaultHost)) {
                return host;
            }
        }
        return null;
    }
}
