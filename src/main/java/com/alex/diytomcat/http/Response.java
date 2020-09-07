package com.alex.diytomcat.http;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author : alexchen
 * @created : 9/5/20, Saturday
 **/
@Log4j2
public class Response extends BaseResponse{

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

    public Response() {
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
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

}
