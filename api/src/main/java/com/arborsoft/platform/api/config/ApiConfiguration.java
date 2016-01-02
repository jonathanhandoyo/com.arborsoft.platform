package com.arborsoft.platform.api.config;

import com.arborsoft.platform.api.handler.ResponseTimeLoggingInterceptor;
import com.arborsoft.platform.core.config.CoreNeo4jConfiguration;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ComponentScan(basePackages = {"com.arborsoft.platform.api"})
@Import(CoreNeo4jConfiguration.class)
@EnableSwagger2
@EnableWebMvc
public class ApiConfiguration extends WebMvcConfigurerAdapter {
    private static final Log LOG = LogFactory.getLog(ApiConfiguration.class);

    @Getter
    @Value(value = "${app.title}")
    private String appTitle;

    @Getter
    @Value(value = "${app.description}")
    private String appDescription;

    @Getter
    @Value(value = "${app.version}")
    private String appVersion;

    @Getter
    @Value(value = "${app.termsOfServiceUrl}")
    private String appTermsOfServiceUrl;

    @Getter
    @Value(value = "${app.contact}")
    private String appContact;

    @Getter
    @Value(value = "${app.license}")
    private String appLicense;

    @Getter
    @Value(value = "${app.licenseUrl}")
    private String appLicenseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ResponseTimeLoggingInterceptor()).addPathPatterns("/rest/**");
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(
                        new ApiInfo(
                                this.appTitle,
                                this.appDescription,
                                this.appVersion,
                                this.appTermsOfServiceUrl,
                                this.appContact,
                                this.appLicense,
                                this.appLicenseUrl
                        )
                )
        ;
    }
}
