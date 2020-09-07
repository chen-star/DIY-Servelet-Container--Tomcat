package com.alex.diytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.alex.diytomcat.util.ServerXMLUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
@Log4j2
public class Service {

    @Getter
    private String name;

    @Getter
    private Engine engine;

    @Getter
    private Server server;

    private List<Connector> connectors;

    public Service(Server server) {
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine(this);
        this.server = server;
        this.connectors = ServerXMLUtil.getConnectors(this);
    }

    public void start() {
        init();
    }

    private void init() {
        TimeInterval timeInterval = DateUtil.timer();
        for (Connector c : connectors) {
            c.init();
        }
        log.info("Initialization processed in {} ms",timeInterval.intervalMs());
        for (Connector c : connectors) {
            c.start();
        }
    }
}
