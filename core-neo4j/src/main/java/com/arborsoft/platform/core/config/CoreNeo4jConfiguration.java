package com.arborsoft.platform.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = {"com.arborsoft.platform.core"})
public class CoreNeo4jConfiguration {
    private static final Log LOG = LogFactory.getLog(CoreNeo4jConfiguration.class);

    private RestGraphDatabase database;
    private RestCypherQueryEngine engine;

    @Autowired
    Environment env;

    @Bean
    public RestGraphDatabase database() {
        this.database = new RestGraphDatabase(
                this.env.getProperty("neo4j.url", "http://localhost:7474/db/data/"),
                this.env.getProperty("neo4j.username", (String) null),
                this.env.getProperty("neo4j.password", (String) null)
        );

        LOG.info(">> neo4j graph database @ " + this.database.getRestAPI().getBaseUri());
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
