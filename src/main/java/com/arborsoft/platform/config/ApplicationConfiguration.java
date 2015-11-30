package com.arborsoft.platform.config;

import com.arborsoft.platform.aspect.RegistrationAspect;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ComponentScan(basePackages = {"com.arborsoft.platform"})
@EnableSwagger2
@EnableWebMvc
public class ApplicationConfiguration extends WebMvcConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfiguration.class);

    private RestGraphDatabase database;
    private RestCypherQueryEngine engine;

    @Bean
    public RestGraphDatabase database() {
        String uri = "http://platform.sb02.stations.graphenedb.com:24789/db/data/";
        String username = "platform";
        String password = "q5Zr4cxHdSG8JCVF5Fve";

        LOG.info(">> neo4j graph database @ " + uri);
        this.database = new RestGraphDatabase(uri, username, password);
        return this.database;
    }

    @Bean
    public RestCypherQueryEngine engine() {
        if (this.engine == null) {
            this.engine = new RestCypherQueryEngine(this.database.getRestAPI());
        }
        LOG.info(">> neo4j cypher engine");
        return this.engine;
    }

    @Bean
    public RegistrationAspect registrationAspect() {
        RegistrationAspect aspect = new RegistrationAspect();
        LOG.info(">> aspect @ " + aspect.getClass().getCanonicalName());
        return aspect;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(new ApiInfo("Platform", null, "1.0-SNAPSHOT", null, "\nJonathan Handoyo <jonathan.handoyo@gmail.com>,\nArnold Palar <arnold.palar@gmail.com>", null, null));
    }
}
