package org.orient.otc.dm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

/**
 * Swagger文档，只有在测试环境才会使用
 *
 * @author FrozenWatermelon
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
	private final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
	private final AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{authorizationScope};
	@Bean
	public Docket baseRestApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
//				.apis(RequestHandlerSelectors.basePackage("org.orient.otc.dm.controller")).paths(PathSelectors.any())
				.build()
				.securitySchemes(Collections.singletonList(apiKey()))
				.securityContexts(Collections.singletonList(securityContext()));
	}

	@Bean
	public ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("数据管理模块").description("数据管理模块接口文档").termsOfServiceUrl("").version("1.0").build();
	}
	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/.*")).build();
	}

	private List<SecurityReference> defaultAuth() {
		return Collections.singletonList(new SecurityReference("Authorization", authorizationScopes));
	}

	private ApiKey apiKey() {
		return new ApiKey("Authorization", "Authorization", "header");
	}
}
