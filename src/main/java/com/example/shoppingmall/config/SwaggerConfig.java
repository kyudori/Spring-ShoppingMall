package com.example.shoppingmall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**") // /api/로 시작하는 경로들만 포함
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Shopping Mall API")
                        .version("1.0.0")
                        .description("Shopping Mall Web API Docs"))
                .addServersItem(new Server().url("http://localhost:8080")); // 필요시 서버 URL 설정
    }
}

