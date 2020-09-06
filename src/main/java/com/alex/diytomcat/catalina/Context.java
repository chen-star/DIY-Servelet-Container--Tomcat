package com.alex.diytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
@Getter
@Setter
@Log4j2
@ToString
public class Context {

    private String path;
    private String docBase;

    public Context(String path, String docBase) {
        TimeInterval timeInterval = DateUtil.timer();
        this.path = path;
        this.docBase = docBase;
        log.info("Deploying web app directory: {}", this.docBase);
        log.info("Deployment of web app directory {} is done in {} ms", this.docBase, timeInterval.intervalMs());
    }
}
