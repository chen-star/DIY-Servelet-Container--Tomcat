package com.alex.diytomcat.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class StandardServletConfig implements ServletConfig {

    private ServletContext servletContext;

    private Map<String, String> initParameters;

    private String servletName;

    public StandardServletConfig(ServletContext servletContext, Map<String, String> initParameters, String servletName) {
        this.servletContext = servletContext;
        this.initParameters = initParameters;
        this.servletName = servletName;
        if (this.initParameters == null) {
            this.initParameters = new HashMap<>();
        }
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String s) {
        return initParameters.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }
}
