package com.alex.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.util.MiniBrowser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author : alexchen
 * @created : 9/5/20, Saturday
 **/
@Log4j2
public class Request {

    @Getter
    private String requestString;

    @Getter
    private String uri;

    private Socket socket;

    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)) {
            return;
        }
        parseUri();
    }

    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is);
        requestString = new String(bytes, StandardCharsets.UTF_8);
    }

    private void parseUri() {
        String temp;

        // GET /index.html?name=alex HTTP/1.1
        temp = StrUtil.subBetween(requestString, " ", " ");

        // no path param
        if (!StrUtil.contains(temp, '?')) {
            uri = temp;
            return;
        }

        temp = StrUtil.subBefore(temp, '?', false);
        uri = temp;
    }
}
