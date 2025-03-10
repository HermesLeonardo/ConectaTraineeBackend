package com.Trainee.ConectaTraineeBackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Conecta Trainee API")
                        .version("1.0")
                        .description("Documentação da API do Conecta Trainee")
                )
                // 🔥 Define o esquema de segurança para o Bearer Token
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("BearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(In.HEADER)
                        )
                        // 🔥 Adiciona um esquema específico para login
                        .addSecuritySchemes("LoginAuth",
                                new SecurityScheme()
                                        .name("LoginAuth")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(In.HEADER)
                        )
                );
    }
}
