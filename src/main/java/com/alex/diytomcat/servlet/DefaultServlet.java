package com.alex.diytomcat.servlet;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.catalina.Context;
import com.alex.diytomcat.http.Request;
import com.alex.diytomcat.http.Response;
import com.alex.diytomcat.util.Constants;
import com.alex.diytomcat.util.WebXmlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class DefaultServlet extends HttpServlet {

    private static DefaultServlet instance = new DefaultServlet();

    private DefaultServlet() {

    }

    public static synchronized DefaultServlet getInstance() {
        return instance;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Request request = (Request) req;
        Response response = (Response) resp;

        Context context = request.getContext();

        String uri = request.getUri();
        if ("/500.html".equals(uri)) {
            throw new RuntimeException("this is a deliberate exception");
        }

        if ("/".equals(uri)) {
            uri = WebXmlUtil.getWelcomeFile(request.getContext());
        }

        String fileName = StrUtil.removePrefix(uri, "/");
        File file = FileUtil.file(request.getRealPath(fileName));

        if (file.exists()) {
            String extension = FileUtil.extName(file);
            String mimeType = WebXmlUtil.getMimeType(extension);
            response.setContentType(mimeType);

            byte[] body = FileUtil.readBytes(file);
            response.setBody(body);

            if (fileName.equals("timeConsume.html")) {
                ThreadUtil.sleep(1000);
            }

            response.setStatus(Constants.CODE_200);
        } else {
            response.setStatus(Constants.CODE_404);
        }
    }
}
