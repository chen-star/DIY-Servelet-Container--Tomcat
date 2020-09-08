package com.alex.diytomcat;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class CustomizedURLClassLoader extends URLClassLoader {

    public CustomizedURLClassLoader(URL[] urls) {
        super(urls);
    }

    public static void main(String[] args) throws Exception {
        URL url = new URL("file:" + System.getProperty("user.dir") +"/test_classloader_jar/hello.jar");
        URL[] urls = new URL[]{url};

        System.out.println(url.getPath());

        CustomizedURLClassLoader loader = new CustomizedURLClassLoader(urls);

        Class<?> aClass = loader.loadClass("Hello");

        Object o = aClass.newInstance();
        Method m = aClass.getMethod("hello");
        m.invoke(o);

        System.out.println(aClass.getClassLoader());

    }

}