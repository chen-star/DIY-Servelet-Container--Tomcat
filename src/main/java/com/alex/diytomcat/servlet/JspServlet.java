package com.alex.diytomcat.servlet;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alex.diytomcat.catalina.Context;
import com.alex.diytomcat.classloader.JspClassLoader;
import com.alex.diytomcat.http.Request;
import com.alex.diytomcat.http.Response;
import com.alex.diytomcat.util.Constants;
import com.alex.diytomcat.util.JspUtil;
import com.alex.diytomcat.util.WebXmlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class JspServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static JspServlet instance = new JspServlet();

    public static synchronized JspServlet getInstance() {
        return instance;
    }

    private JspServlet() {

    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws IOException, ServletException {
        try {
            Request request = (Request) httpServletRequest;
            Response response = (Response) httpServletResponse;

            String uri = request.getRequestURI();

            if ("/".equals(uri))
                uri = WebXmlUtil.getWelcomeFile(request.getContext());

            String fileName = StrUtil.removePrefix(uri, "/");
            File file = FileUtil.file(request.getRealPath(fileName));

            System.out.println("JSP file name " + fileName);

            File jspFile = file;
            if (jspFile.exists()) {
                Context context = request.getContext();
                String path = context.getPath();
                String subFolder;
                if ("/".equals(path))
                    subFolder = "_";
                else
                    subFolder = StrUtil.subAfter(path, '/', false);

                String servletClassPath = JspUtil.getServletClassPath(uri, subFolder);
                File jspServletClassFile = new File(servletClassPath);
                if (!jspServletClassFile.exists()) {
                    JspUtil.compileJsp(context, jspFile);
                } else if (jspFile.lastModified() > jspServletClassFile.lastModified()) {
                    JspUtil.compileJsp(context, jspFile);
                    JspClassLoader.invalidJspClassLoader(uri, context);
                }

                String extName = FileUtil.extName(file);
                String mimeType = WebXmlUtil.getMimeType(extName);
                response.setContentType(mimeType);

                JspClassLoader jspClassLoader = JspClassLoader.getJspClassLoader(uri, context);
                String jspServletClassName = JspUtil.getJspServletClassName(uri, subFolder);
                Class jspServletClass = jspClassLoader.loadClass(jspServletClassName);

                HttpServlet servlet = context.getServlet(jspServletClass);
                servlet.service(request, response);

                if (null != response.getRedirectPath()) {
                    response.setStatus(Constants.CODE_302);
                } else {
                    response.setStatus(Constants.CODE_200);
                }
            } else {
                response.setStatus(Constants.CODE_404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
