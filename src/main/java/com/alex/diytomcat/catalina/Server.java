package com.alex.diytomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.alex.diytomcat.http.Request;
import com.alex.diytomcat.http.Response;
import com.alex.diytomcat.util.Constants;
import com.alex.diytomcat.util.ThreadPoolUtil;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
@Log4j2
public class Server {

    private Service service;

    private static final int PORT = 18080;

    public Server(){
        this.service = new Service(this);
    }

    public void start() {
        logJVM();
        init();
    }

    private void init() {
        try {
            ServerSocket ss = new ServerSocket(PORT);

            while (true) {
                // receive a request
                final Socket s = ss.accept();

                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            // wrap Request entity
                            Request request = new Request(s, service);
                            System.out.println("Request is \r\n" + request.getRequestString());
                            Context context = request.getContext();

                            // wrap Response entity
                            Response response = new Response();

                            String uri = request.getUri();
                            if (StrUtil.isEmpty(uri)) {
                                return;
                            }
                            log.info("Request uri={}", uri);

                            if (uri.equals("/")) {
                                String html = "Hello From Alex's DIY Tomcat";
                                response.getWriter().println(html);
                            } else {
                                String fileName = StrUtil.removePrefix(uri, "/");
                                File file = FileUtil.file(context.getDocBase(),fileName);
                                if (file.exists()) {
                                    String fileContent = FileUtil.readUtf8String(file);
                                    response.getWriter().println(fileContent);

                                    if (fileName.equals("timeConsume.html")) {
                                        ThreadUtil.sleep(1000);
                                    }

                                } else {
                                    response.getWriter().println("404 File Not Found");
                                }
                            }


                            handle200(s, response);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    }
                };

                ThreadPoolUtil.run(r);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    private static void handle200(Socket s, Response response) throws IOException {

        String contentType = response.getContentType();
        String headerText = Constants.response_header_200;
        headerText = StrUtil.format(headerText, contentType);

        byte[] header = headerText.getBytes();
        byte[] body = response.getBody();

        byte[] responseBytes = new byte[header.length + body.length];
        ArrayUtil.copy(header, 0, responseBytes, 0, header.length);
        ArrayUtil.copy(body, 0, responseBytes, header.length, body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
        s.close();
    }

    private static void logJVM() {
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Server version", "Alex's Tomcat - v1.0.0");
        infos.put("Server built", "2020-09-04 10:20:22");
        infos.put("Server number", "1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));
        Set<String> keys = infos.keySet();
        for (String key : keys) {
            log.info(key + ":\t\t" + infos.get(key));
        }
    }

}
