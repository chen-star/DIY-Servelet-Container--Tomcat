package com.alex.javaweb;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : alexchen
 * @created : 9/8/20, Tuesday
 **/
public class ServerJumpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setAttribute("name", "Alex");
            request.getRequestDispatcher("hello").forward(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        }

    }
}