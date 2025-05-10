package kz.sdu.booking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
		title = "Chat-API", version = "v1"),
		servers = @Server(url = "/"),
		security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		in = SecuritySchemeIn.HEADER,
		paramName = "Authorization",
		bearerFormat = "JWT",
		scheme = "bearer"
)
class OpenApiConfiguration {
}