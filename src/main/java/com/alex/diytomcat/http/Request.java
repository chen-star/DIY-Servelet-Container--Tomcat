package com.alex.diytomcat.http;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alex.diytomcat.catalina.Context;
import com.alex.diytomcat.catalina.Service;
import com.alex.diytomcat.util.MiniBrowser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : alexchen
 * @created : 9/5/20, Saturday
 **/
@Log4j2
public class Request extends BaseRequest {

    public static final String prefix = "/";

    @Getter
    private String requestString;

    @Getter
    private String uri;

    @Getter
    private String method;

    private Socket socket;

    @Getter
    private Service service;

    @Getter
    @Setter
    private Context context;

    private String queryString;

    @Getter
    private Map<String, String[]> parameterMap;

    public Request(Socket socket, Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        this.parameterMap = new HashMap<>();
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString)) {
            return;
        }
        parseUri();
        parseContext();
        parseMethod();
        parseParameters();

        if (!prefix.equals(context.getPath())) {
            uri = StrUtil.removePrefix(uri, context.getPath());
            if (StrUtil.isEmpty(uri)) {
                uri = prefix;
            }
        }
        log.info("Request -- {} with Context -- {}", this.uri, this.context);
    }

    public String getParameter(String name) {
        String[] values = parameterMap.get(name);
        if (values != null && values.length != 0) {
            return values[0];
        }
        return null;
    }

    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }

    public Enumeration getParameterNames() {
        return Collections.enumeration(parameterMap.keySet());
    }

    private void parseMethod() {
        method = StrUtil.subBefore(requestString, " ", false);
    }

    private void parseContext() {
        context = service.getEngine().getDefaultHost().getContext(uri);
        if (null != context) {
            return;
        }
        String path = StrUtil.subBetween(uri, prefix, prefix);
        if (StrUtil.isEmpty(path)) {
            path = prefix;
        } else {
            path = prefix + path;
        }

        context = service.getEngine().getDefaultHost().getContext(path);
        if (null == context) {
            context = service.getEngine().getDefaultHost().getContext(prefix);
        }
    }

    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is, false);
        requestString = new String(bytes, StandardCharsets.UTF_8);
    }

    private void parseParameters() {
        if ("GET".equals(this.getMethod())) {
            String url = StrUtil.subBetween(requestString, " ", " ");
            if (StrUtil.contains(url, '?')) {
                queryString = StrUtil.subAfter(url, '?', false);
            }
        }
        if ("POST".equals(this.getMethod())) {
            queryString = StrUtil.subAfter(requestString, "\r\n\r\n", false);
        }
        if (null == queryString)
            return;
        queryString = URLUtil.decode(queryString);
        String[] parameterValues = queryString.split("&");
        if (null != parameterValues) {
            for (String parameterValue : parameterValues) {
                String[] nameValues = parameterValue.split("=");
                String name = nameValues[0];
                String value = nameValues[1];
                String values[] = parameterMap.get(name);
                if (null == values) {
                    values = new String[]{value};
                    parameterMap.put(name, values);
                } else {
                    values = ArrayUtil.append(values, value);
                    parameterMap.put(name, values);
                }
            }
        }
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

    @Override
    public ServletContext getServletContext() {
        return context.getServletContext();
    }

    @Override
    public String getRealPath(String path) {
        return getServletContext().getRealPath(path);
    }
}
