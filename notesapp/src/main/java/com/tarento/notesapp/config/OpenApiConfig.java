package com.tarento.notesapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Note-Taking API",
        version = "v1",
        description = "API endpoints for managing notes, tags, and folders. Supports Markdown content.",
        contact = @io.swagger.v3.oas.annotations.info.Contact(name = "Your Name")
    )
)
public class OpenApiConfig {
    // No code needed inside the class, the annotation does the work.
}