package com.alex.diytomcat.catalina;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alex.diytomcat.http.Request;
import com.alex.diytomcat.http.Response;
import com.alex.diytomcat.servlet.DefaultServlet;
import com.alex.diytomcat.servlet.InvokerServlet;
import com.alex.diytomcat.servlet.JspServlet;
import com.alex.diytomcat.util.Constants;
import com.alex.diytomcat.util.SessionManager;
import lombok.extern.log4j.Log4j2;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

            prepareSession(request, response);

            String servletClassName = context.getServletClassName(uri);
            HttpServlet workingServlet;

            if (null != servletClassName)
                workingServlet = InvokerServlet.getInstance();
            else if (uri.endsWith(".jsp"))
                workingServlet = JspServlet.getInstance();
            else
                workingServlet = DefaultServlet.getInstance();

            List<Filter> filters = request.getContext().getMatchedFilters(request.getRequestURI());
            ApplicationFilterChain filterChain = new ApplicationFilterChain(filters, workingServlet);
            filterChain.doFilter(request, response);

            if (request.isForwarded())
                return;

            if (response.getStatus() == Constants.CODE_200) {
                handle200(s, request, response);
                return;
            }
            if (response.getStatus() == Constants.CODE_302) {
                handle302(s, response);
                return;
            }
            if (response.getStatus() == Constants.CODE_404) {
                handle404(s, uri);
                return;
            }

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

    public void prepareSession(Request request, Response response) {
        String jsessionid = request.getJSessionIdFromCookie();
        HttpSession session = SessionManager.getSession(jsessionid, request, response);
        request.setSession(session);
    }

    private void handle200(Socket s, Request request, Response response) throws IOException {

        OutputStream os = s.getOutputStream();
        String contentType = response.getContentType();
        byte[] body = response.getBody();
        String cookiesHeader = response.getCookiesHeader();
        boolean gzip = isGzip(request, body, contentType);
        String headText;
        if (gzip)
            headText = Constants.response_header_200_gzip;
        else
            headText = Constants.response_header_200;
        headText = StrUtil.format(headText, contentType, cookiesHeader);
        if (gzip)
            body = ZipUtil.gzip(body);
        byte[] head = headText.getBytes();
        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);
        os.write(responseBytes, 0, responseBytes.length);
        os.flush();
        os.close();
    }

    private void handle302(Socket s, Response response) throws IOException {
        OutputStream os = s.getOutputStream();
        String redirectPath = response.getRedirectPath();
        String head_text = Constants.response_header_302;
        String header = StrUtil.format(head_text, redirectPath);
        byte[] responseBytes = header.getBytes(StandardCharsets.UTF_8);
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

    private boolean isGzip(Request request, byte[] body, String mimeType) {
        String acceptEncodings = request.getHeader("Accept-Encoding");
        if (!StrUtil.containsAny(acceptEncodings, "gzip"))
            return false;


        Connector connector = request.getConnector();
        if (mimeType.contains(";"))
            mimeType = StrUtil.subBefore(mimeType, ";", false);
        if (!"on".equals(connector.getCompression()))
            return false;
        if (body.length < connector.getCompressionMinSize())
            return false;
        String userAgents = connector.getNoCompressionUserAgents();
        String[] eachUserAgents = userAgents.split(",");
        for (String eachUserAgent : eachUserAgents) {
            eachUserAgent = eachUserAgent.trim();
            String userAgent = request.getHeader("User-Agent");
            if (StrUtil.containsAny(userAgent, eachUserAgent))
                return false;
        }
        String mimeTypes = connector.getCompressableMimeType();
        String[] eachMimeTypes = mimeTypes.split(",");
        for (String eachMimeType : eachMimeTypes) {
            if (mimeType.equals(eachMimeType))
                return true;
        }
        return false;
    }
}
