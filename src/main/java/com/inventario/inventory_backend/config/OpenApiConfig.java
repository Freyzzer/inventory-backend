package com.inventario.inventory_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Backend API")
                        .description("API REST para sistema de seguimiento de inventario")
                        .version("1.0.0")
                        .contact(new Contact().name("Inventory Team"))
                        .license(new License().name("MIT")));
    }
}
