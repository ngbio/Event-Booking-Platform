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
            ApiSecurityConfigs.class
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
        long maxFileSize = 5242880; // 5MB
        long maxRequestSize = 20971520; // 20MB
        int fileSizeThreshold = 0;

        registration.setMultipartConfig(new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold));
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");
    }

}
