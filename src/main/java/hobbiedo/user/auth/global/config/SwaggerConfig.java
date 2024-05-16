package hobbiedo.user.auth.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	private static final String BEARER_TOKEN = "Bearer ";
	private static final String BEARER_SCHEME = "bearer";
	private static final String BEARER_FORMAT = "JWT";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.components(new Components())
				.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
				.title("취미한다(DO) SpringDoc")
				.description("SpringDoc을 사용한 Swagger UI 테스트")
				.version("1.0.0");
	}
}
