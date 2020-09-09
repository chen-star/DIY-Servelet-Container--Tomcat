package com.alex.javaweb;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author : alexchen
 * @created : 9/8/20, Tuesday
 **/
public class ContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        System.out.println("Listening on " + e.getSource() + " [Destroy]  ");
    }

    @Override
    public void contextInitialized(ServletContextEvent e) {
        System.out.println("Listening on " + e.getSource() + " [Init] ");
    }
}

