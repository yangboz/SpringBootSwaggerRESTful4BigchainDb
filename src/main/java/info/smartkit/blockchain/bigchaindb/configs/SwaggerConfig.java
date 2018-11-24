package info.smartkit.blockchain.bigchaindb.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
/**
 * The Class SwaggerConfig.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("info.smartkit.blockchain.bigchaindb.controllers"))
//                .paths(PathSelectors.ant("bigchaindb/*"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(apiInfo());
		//                              .useDefaultResponseMessages(false)
//                              .globalResponseMessage(RequestMethod.GET,
//                                              new ArrayList(new ResponseMessageBuilder()
//                                                                              .code(500)
//                                                                              .message("500 message")
//                                                                              .responseModel(new ModelRef("Error"))
//                                                                              .build(),
//                                                              new ResponseMessageBuilder()
//                                                                              .code(403)
//                                                                              .message("Forbidden!")
//                                                                              .build()));
	}
	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo(
				"BLOCKCHAIN.SMARTKIT.INFO REST API",
				"SMARTKIT.INFO description of API.",
				"API TOS",
				"Terms of service",
				"contact@smartkit.info",
				"License of API",
				"API license URL");
		return apiInfo;
	}
}