package com.alex.diytomcat.http;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.Cookie;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author : alexchen
 * @created : 9/5/20, Saturday
 **/
@Log4j2
public class Response extends BaseResponse {

    private StringWriter stringWriter;

    @Getter
    private PrintWriter writer;

    @Setter
    private byte[] body;

    @Getter
    @Setter
    private String contentType;

    @Getter
    @Setter
    private int status;

    @Getter
    private List<Cookie> cookies;

    @Getter
    private String redirectPath;

    public Response() {
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
        this.cookies = new ArrayList<>();
    }

    public byte[] getBody() throws UnsupportedEncodingException {
        // text based
        if (null == body) {
            String content = stringWriter.toString();
            return content.getBytes(StandardCharsets.UTF_8);
        }
        // byte stream
        return body;
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public String getCookiesHeader() {
        if (null == cookies)
            return "";
        String pattern = "EEE, d MMM yyyy HH:mm:ss 'GMT'";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        StringBuffer sb = new StringBuffer();
        for (Cookie cookie : getCookies()) {
            sb.append("\r\n");
            sb.append("Set-Cookie: ");
            sb.append(cookie.getName() + "=" + cookie.getValue() + "; ");
            if (-1 != cookie.getMaxAge()) { //-1 mean forever
                sb.append("Expires=");
                Date now = new Date();
                Date expire = DateUtil.offset(now, DateField.MINUTE, cookie.getMaxAge());
                sb.append(sdf.format(expire));
                sb.append("; ");
            }
            if (null != cookie.getPath()) {
                sb.append("Path=" + cookie.getPath());
            }
        }
        return sb.toString();
    }

    public void sendRedirect(String redirect) {
        this.redirectPath = redirect;
    }

}
