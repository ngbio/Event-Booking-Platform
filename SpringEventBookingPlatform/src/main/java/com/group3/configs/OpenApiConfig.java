package com.group3.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.configuration.SpringDocUIConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    SpringDocConfiguration.class,
    SpringDocConfigProperties.class,
    SwaggerUiConfigProperties.class,
    SwaggerUiOAuthProperties.class,
    SwaggerConfig.class,
    SpringDocUIConfiguration.class
})
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation for Event Booking Platform")
                        .version("1.0.0")
                        .description("REST API documentation for Spring Event Booking Platform.")
                        .contact(new Contact()
                                .name("Group 3")));
    }
}