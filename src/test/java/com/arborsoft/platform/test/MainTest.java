package com.arborsoft.platform.test;

import com.arborsoft.platform.Application;
import com.arborsoft.platform.service.Neo4jService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class MainTest {
    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);

    @Autowired
    private Neo4jService neo4j;

    @Test
    public void test1() throws Exception {
        LOG.info("Hello World!");
        LOG.info(this.neo4j.get(9l).toString());
    }
}
