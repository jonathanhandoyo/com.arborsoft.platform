package com.arborsoft.platform.web.config;

import com.arborsoft.platform.core.config.CoreNeo4jConfiguration;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan(basePackages = {"com.arborsoft.platform.web"})
@Import(CoreNeo4jConfiguration.class)
@EnableWebMvc
@EnableAutoConfiguration
public class WebConfiguration extends WebMvcConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(WebConfiguration.class);

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
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public InternalResourceViewResolver setupViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
}
