package com.alex.javaweb;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class HelloServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response){
        try {
            response.getWriter().println("Hello DIY Tomcat from HelloServlet@javaweb");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
