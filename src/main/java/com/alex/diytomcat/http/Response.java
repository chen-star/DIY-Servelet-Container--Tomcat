package com.alex.diytomcat.http;

import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author : alexchen
 * @created : 9/5/20, Saturday
 **/
public class Response {

    private StringWriter stringWriter;

    @Getter
    private PrintWriter writer;

    @Getter
    private String contentType;

    public Response() {
        this.stringWriter = new StringWriter();
        this.writer = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }

    public byte[] getBody() throws UnsupportedEncodingException {
        String content = stringWriter.toString();
        return content.getBytes(StandardCharsets.UTF_8);
    }
}
