package com.arborsoft.platform.api.config;

import com.arborsoft.platform.core.service.Neo4jService;
import org.mockito.Mockito;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestApiConfiguration {
    @Bean
    public Neo4jService neo4jService() {
        return Mockito.mock(Neo4jService.class);
    }

    @Bean
    public RestGraphDatabase database() {
        return Mockito.mock(RestGraphDatabase.class);//TODO: can use impermanent neo4j ??
    }

    @Bean
    public RestCypherQueryEngine engine() {
        return Mockito.mock(RestCypherQueryEngine.class);
    }
}
