package com.group3.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.context.annotation.Bean;

@Configuration
@ComponentScan(
        basePackages = {
            "com.group3.controllers",
            "com.group3.repository",
            "com.group3.service",
            "org.springdoc",
            "com.group3.exceptions"
        }
)
@EnableWebMvc
@EnableTransactionManagement
@EnableAsync
public class WebAppContextConfigs implements WebMvcConfigurer{

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
         configurer.enable();
    }

    //http://localhost:8080/SpringEventBookingPlatform/swagger-ui/index.html
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .resourceChain(false);
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);
    }

    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    @Bean
    public org.springframework.validation.beanvalidation.LocalValidatorFactoryBean validator() {
        return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
    }
}
