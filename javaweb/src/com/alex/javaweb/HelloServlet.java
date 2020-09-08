package com.alex.javaweb;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class HelloServlet extends HttpServlet {

    public HelloServlet() {
        System.out.println(this + " [Constructor]");
    }

    public void init(ServletConfig config){
        String author = config.getInitParameter("author");
        String site = config.getInitParameter("site");

        System.out.println(this + " [Init]");
        System.out.println("param author:" + author);
        System.out.println("param site:" + site);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println(this + " [doGet()]");
            Class clazz = Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(clazz);
            System.out.println(clazz.getClassLoader());
            response.getWriter().println("Hello DIY Tomcat from HelloServlet@javaweb" + this);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void destroy(){
        System.out.println(this + " [Destroy]");
    }

}
