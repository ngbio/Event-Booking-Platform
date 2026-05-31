package com.group3.configs;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource("classpath:configs.properties")
public class MailConfig {

    @Autowired
    private Environment env;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(env.getProperty("mail.host", "smtp.gmail.com"));
        sender.setPort(Integer.parseInt(env.getProperty("mail.port", "587")));
        sender.setUsername(env.getProperty("mail.username", ""));
        sender.setPassword(env.getProperty("mail.password", ""));
        sender.setProtocol(env.getProperty("mail.protocol", "smtp"));
        sender.setDefaultEncoding("UTF-8");

        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", env.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.timeout", env.getProperty("mail.smtp.timeout", "10000"));
        props.put("mail.smtp.connectiontimeout", env.getProperty("mail.smtp.connectiontimeout", "10000"));
        return sender;
    }
}
