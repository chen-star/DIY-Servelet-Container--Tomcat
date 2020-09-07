package com.alex.diytomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.http.Request;
import com.alex.diytomcat.http.Response;
import com.alex.diytomcat.util.Constants;
import com.alex.diytomcat.util.WebXmlUtil;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author : alexchen
 * @created : 9/6/20, Sunday
 **/
@Log4j2
public class HttpProcessor {

    public void execute(Socket s, Request request, Response response) {
        try {

            Context context = request.getContext();
            String uri = request.getUri();
            if (StrUtil.isEmpty(uri)) {
                return;
            }
            log.info("Request uri={}", uri);

            // for 500 demo purpose
            if ("/500.html".equals(uri)) {
                throw new Exception("this is a deliberate exception");
            }

            if ("/".equals(uri))
                uri = WebXmlUtil.getWelcomeFile(request.getContext());

            if (uri.equals("/")) {
                String html = "Hello From Alex's DIY Tomcat";
                response.getWriter().println(html);
            } else {
                String fileName = StrUtil.removePrefix(uri, "/");
                File file = FileUtil.file(context.getDocBase(), fileName);
                if (file.exists()) {
                    String extension = FileUtil.extName(file);
                    String mimeType = WebXmlUtil.getMimeType(extension);
                    log.info("MimeType={}", mimeType);
                    response.setContentType(mimeType);
                    byte[] body = FileUtil.readBytes(file);
                    response.setBody(body);

                    // for multi tasks demo purpose
                    if (fileName.equals("timeConsume.html")) {
                        ThreadUtil.sleep(1000);
                    }

                } else {
                    handle404(s, uri);
                    return;
                }
            }

            handle200(s, response);
        } catch (
                Exception e) {
            log.error(e.getMessage());
            handle500(s, e);
        } finally {
            try {
                if (!s.isClosed()) {
                    s.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }

    private static void handle404(Socket s, String uri) throws IOException {
        OutputStream os = s.getOutputStream();
        String responseText = StrUtil.format(Constants.textFormat_404, uri, uri);
        responseText = Constants.response_header_404 + responseText;
        byte[] responseByte = responseText.getBytes(StandardCharsets.UTF_8);
        os.write(responseByte);
    }

    private static void handle500(Socket s, Exception e) {
        OutputStream os = null;
        try {
            os = s.getOutputStream();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        StackTraceElement[] stackTraceElement = e.getStackTrace();
        StringBuffer sb = new StringBuffer();
        sb.append(e.toString());
        sb.append("\r\n");
        for (StackTraceElement element : stackTraceElement) {
            sb.append("\t");
            sb.append(element.toString());
            sb.append("\r\n");
        }

        String msg = e.getMessage();
        if (!StrUtil.isEmpty(msg) && msg.length() > 30) {
            msg = msg.substring(0, 29);
        }

        String text = StrUtil.format(Constants.textFormat_500, msg, e.toString(), sb.toString());
        text = Constants.response_header_500 + text;
        byte[] responseByte = text.getBytes(StandardCharsets.UTF_8);
        try {
            os.write(responseByte);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
