package com.alex.diytomcat.catalina;

import com.alex.diytomcat.http.Request;
import com.alex.diytomcat.http.Response;
import com.alex.diytomcat.util.ThreadPoolUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
@Log4j2
public class Connector implements Runnable {

    @Getter
    @Setter
    int port;

    @Getter
    private Service service;

    @Getter
    @Setter
    private String compression;

    @Getter
    @Setter
    private int compressionMinSize;

    @Getter
    @Setter
    private String noCompressionUserAgents;

    @Getter
    @Setter
    private String compressableMimeType;

    public Connector(Service service) {
        this.service = service;
    }


    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket s = ss.accept();
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s, Connector.this);
                            Response response = new Response();
                            HttpProcessor processor = new HttpProcessor();
                            processor.execute(s, request, response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (!s.isClosed()) {
                                try {
                                    s.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                };

                ThreadPoolUtil.run(r);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        log.info("Initializing ProtocolHandler [http-bio-{}]", port);
    }

    public void start() {
        log.info("Starting ProtocolHandler [http-bio-{}]", port);
        new Thread(this).start();
    }

}
