package com.bureau.translateit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(
                        new Info()
                                .title("TranslateIt API")
                                .version("v1")
                                .description("TranslateIt API developed with Spring and PostgreSQL for Bureau Works Test.")
                                .license(new License().name("MIT").url("https://www.mit.edu/~amini/LICENSE.md"))
                )
                .tags(getTags());
    }

    private List<Tag> getTags() {
        Tag tag1 = new Tag();
        tag1.setName("Translators");
        tag1.setDescription("Translators management routes.");

        Tag tag2 = new Tag();
        tag2.setName("Documents");
        tag2.setDescription("Documents management routes.");

        return Arrays.asList(tag1, tag2);
    }
}