package com.arborsoft.platform.core.config;

import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.arborsoft.platform.core"})
public class CoreNeo4jConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CoreNeo4jConfiguration.class);

    private RestGraphDatabase database;
    private RestCypherQueryEngine engine;

    @Bean
    public RestGraphDatabase database() {
        //String uri = "http://platform.sb02.stations.graphenedb.com:24789/db/data/";
        String uri = "http://10.80.46.243:7474/db/data/";
        String username = null;
        String password = null;

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
}
