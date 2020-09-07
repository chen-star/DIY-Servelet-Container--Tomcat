package com.alex.diytomcat.catalina;

import com.alex.diytomcat.util.ServerXMLUtil;
import lombok.Getter;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
public class Service {

    @Getter
    private String name;

    @Getter
    private Engine engine;

    @Getter
    private Server server;

    public Service(Server server) {
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine(this);
        this.server = server;
    }
}
