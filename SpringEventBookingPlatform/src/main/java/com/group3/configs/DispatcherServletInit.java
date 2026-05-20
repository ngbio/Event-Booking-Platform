package com.group3.configs;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DispatcherServletInit extends AbstractAnnotationConfigDispatcherServletInitializer  {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
            HibernateConfigs.class,
            ThymeleafConfigs.class,
            SpringSecurityConfigs.class
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
    
}
