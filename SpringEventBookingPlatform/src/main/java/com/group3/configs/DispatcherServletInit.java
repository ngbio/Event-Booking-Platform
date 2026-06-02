package com.group3.configs;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DispatcherServletInit extends AbstractAnnotationConfigDispatcherServletInitializer  {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
            HibernateConfigs.class,
            ThymeleafConfigs.class,
            SpringSecurityConfigs.class,
            ApiSecurityConfigs.class,
            MailConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
       return new Class[]{
           WebAppContextConfigs.class,
           OpenApiConfig.class,
       };
    }
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        String location = "/";
        long maxFileSize = 20L * 1024 * 1024; 
        long maxRequestSize = 40L * 1024 * 1024;
        int fileSizeThreshold = 0;

        registration.setMultipartConfig(new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold));
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
    }

}
