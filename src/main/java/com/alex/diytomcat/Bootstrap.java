package com.alex.diytomcat;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.http.Request;
import com.alex.diytomcat.http.Response;
import com.alex.diytomcat.util.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

public class Bootstrap {

    public static void main(String[] args) {

        try {
            int port = 18080;

            if (!NetUtil.isUsableLocalPort(port)) {
                System.out.println(port + " is in use");
                return;
            }
            ServerSocket ss = new ServerSocket(port);

            while (true) {
                // wrap Request entity
                Socket s = ss.accept();
                Request request = new Request(s);
                System.out.println("Request is \r\n" + request.getRequestString());

                // wrap Response entity
                Response response = new Response();
                String html = "Hello From Alex's DIY Tomcat";
                response.getWriter().println(html);

                handle200(s, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
}