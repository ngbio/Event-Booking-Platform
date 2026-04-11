/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group3.configs;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 *
 * @author lenovo
 */
public class DispatcherServletInit extends AbstractAnnotationConfigDispatcherServletInitializer  {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
            HibernateConfigs.class,
            ThymeleafConfigs.class,
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
       return new Class[]{
           WebAppContextConfigs.class,
       };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
    
}
