package com.alex.diytomcat;

import com.alex.diytomcat.classloader.CommonClassLoader;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Method;

@Log4j2
public class Bootstrap {

    private static final int PORT = 18080;

//    public static void main(String[] args) throws Exception {
//
//        log.info("Tomcat starts up");
//
//        Server server = new Server();
//        server.start();
//    }


    public static void main(String[] args) throws Exception {
        CommonClassLoader commonClassLoader = new CommonClassLoader();

        Thread.currentThread().setContextClassLoader(commonClassLoader);

        String serverClassName = "com.alex.diytomcat.catalina.Server";

        Class<?> serverClazz = commonClassLoader.loadClass(serverClassName);

        Object serverObject = serverClazz.newInstance();

        Method m = serverClazz.getMethod("start");

        m.invoke(serverObject);

        System.out.println(serverClazz.getClassLoader());
    }
}