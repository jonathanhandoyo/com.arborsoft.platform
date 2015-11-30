package com.arborsoft.platform.test;

import com.arborsoft.platform.Application;
import com.arborsoft.platform.domain.BaseNode;
import com.arborsoft.platform.service.Neo4jService;
import org.junit.After;
import org.junit.Before;
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
    private long start;

    @Autowired
    private Neo4jService neo4j;

    @Before
    public void before() throws Exception {
        this.neo4j.clear();
        start = System.currentTimeMillis();
    }

    @After
    public void after() throws Exception {
        System.out.println("time taken: " + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test1() throws Exception {
        BaseNode one = new BaseNode();
        one.addLabel("Label-A");
        one.addLabel("Label-B");
        one.set("key-A", "value-A");
        one.set("key-B", "value-B");
        one = this.neo4j.save(one);

        BaseNode two = new BaseNode();
        two.addLabel("Label-B");
        two.addLabel("Label-C");
        two.set("key-B", "value-B");
        two.set("key-C", "value-C");
        two = this.neo4j.save(two);
    }
}
