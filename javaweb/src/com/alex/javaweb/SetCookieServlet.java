package com.alex.javaweb;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class SetCookieServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie c = new Cookie("name", "Alex(cookie)");
            c.setMaxAge(60 * 24 * 60);
            c.setPath("/");
            response.addCookie(c);
            response.getWriter().println("set cookie successfully! " + c.getName() + ":" + c.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}