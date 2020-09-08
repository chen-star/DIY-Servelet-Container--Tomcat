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
            Class clazz= Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println(clazz);
            System.out.println(clazz.getClassLoader());
            response.getWriter().println("Hello DIY Tomcat from HelloServlet@javaweb");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
