package com.alex.diytomcat;

import com.alex.diytomcat.catalina.Server;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Bootstrap {

    private static final int PORT = 18080;

    public static void main(String[] args) {

        log.info("Tomcat starts up");

        Server server = new Server();
        server.start();

    }
}