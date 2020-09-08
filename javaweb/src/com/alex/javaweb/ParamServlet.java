package com.alex.javaweb;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class ParamServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String name = request.getParameter("name");
            response.getWriter().println("get name:" + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String name = request.getParameter("name");
            response.getWriter().println("post name:" + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
