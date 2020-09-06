package com.alex.diytomcat.util;

import cn.hutool.system.SystemUtil;

import java.io.File;

/**
 * @author : alexchen
 * @created : 9/5/20, Saturday
 **/
public class Constants {

    public final static String response_header_200 = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: {}\r\n\r\n";

    public final static File webappsFolder = new File(SystemUtil.get("user.dir"), "webapps");

    public final static File rootFolder = new File(webappsFolder, "ROOT");

    public static final File confFolder = new File(SystemUtil.get("user.dir"), "conf");

    public static final File serverXmlFile = new File(confFolder, "server.xml");
}
