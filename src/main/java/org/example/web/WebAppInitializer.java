package org.example.web;

import org.apache.log4j.Logger;
import org.example.web.config.WebContextConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {

    Logger logger = Logger.getLogger(WebAppInitializer.class);

    @Override
    public void onStartup(javax.servlet.ServletContext servletContext)  {

        logger.info("loading app config");
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("classpath:app-config.xml");
        servletContext.addListener(new ContextLoaderListener(appContext));

        logger.info("loading web config");
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebContextConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        logger.info("dispatcher ready");
    }
}
