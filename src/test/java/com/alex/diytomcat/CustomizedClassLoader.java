package com.alex.diytomcat;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author : alexchen
 * @created : 9/7/20, Monday
 **/
public class CustomizedClassLoader extends ClassLoader {

    private File classesFolder = new File(System.getProperty("user.dir"), "test_classloader_class");

    @Override
    protected Class<?> findClass(String QualifiedName) throws ClassNotFoundException {
        byte[] data = loadClassData(QualifiedName);
        return defineClass(QualifiedName, data,0, data.length);
    }

    private byte[] loadClassData(String fullQualifiedName) throws ClassNotFoundException {
        String fileName = StrUtil.replace(fullQualifiedName, ".", "/") +  ".class";
        File classFile = new File(classesFolder, fileName);
        System.out.println(classFile.getAbsolutePath());
        if (!classFile.exists()) {
            throw new ClassNotFoundException(fullQualifiedName);
        }
        return FileUtil.readBytes(classFile);
    }

    public static void main(String[] args) throws Exception {

        CustomizedClassLoader loader = new CustomizedClassLoader();

        Class<?> how2jClass = loader.loadClass("Hello");

        Object o = how2jClass.newInstance();

        Method m = how2jClass.getMethod("hello");

        m.invoke(o);

        System.out.println(how2jClass.getClassLoader());

    }
}
