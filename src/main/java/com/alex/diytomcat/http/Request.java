package com.alex.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.Bootstrap;
import com.alex.diytomcat.catalina.Context;
import com.alex.diytomcat.util.MiniBrowser;
import lombok.Getter;
import lombok.Setter;
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

    public static final String prefix = "/";

    @Getter
    private String requestString;

    @Getter
    private String uri;

    private Socket socket;

    @Getter
    @Setter
    private Context context;

    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)) {
            return;
        }
        parseUri();
        parseContext();
        if(!prefix.equals(context.getPath()))
            uri = StrUtil.removePrefix(uri, context.getPath());
        log.info("Request -- {} with Context -- {}", this.uri, this.context);
    }

    private void parseContext() {
        String path = StrUtil.subBetween(uri, prefix, prefix);
        if (StrUtil.isEmpty(path)) {
            path = prefix;
        } else {
            path = prefix + path;
        }

        context = Bootstrap.getContextMap().get(path);
        if (null == context) {
            context = Bootstrap.getContextMap().get(prefix);
        }
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
