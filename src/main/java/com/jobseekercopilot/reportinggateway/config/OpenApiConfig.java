package com.jobseekercopilot.reportinggateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI reportingGatewayOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Jobseeker Copilot - Reporting Gateway API")
                .description("Frontend-facing reporting gateway.")
                .version("1.0.0"));
    }
}
