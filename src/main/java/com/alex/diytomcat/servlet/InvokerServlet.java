package com.alex.diytomcat.servlet;

import cn.hutool.core.util.ReflectUtil;
import com.alex.diytomcat.catalina.Context;
import com.alex.diytomcat.http.Request;
import com.alex.diytomcat.http.Response;
import com.alex.diytomcat.util.Constants;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handle Requests goto servlets
 *
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
@Log4j2
@NoArgsConstructor
public class InvokerServlet extends HttpServlet {

    private static InvokerServlet instance = new InvokerServlet();

    public static synchronized InvokerServlet getInstance() {
        return instance;
    }

    public void service(HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;

        String uri = request.getUri();
        Context context = request.getContext();
        String servletClassName = context.getServletClassName(uri);

        try {
            Class servletClass = context.getWebappClassLoader().loadClass(servletClassName);
            System.out.println("ServletClass: " + servletClass);
            System.out.println("ServletClass' classLoader" + servletClass.getClassLoader());
            Object servletObject = context.getServlet(servletClass);
            ReflectUtil.invoke(servletObject, "service", request, response);
            if (null != response.getRedirectPath()) {
                response.setStatus(Constants.CODE_302);
            } else {
                response.setStatus(Constants.CODE_200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
