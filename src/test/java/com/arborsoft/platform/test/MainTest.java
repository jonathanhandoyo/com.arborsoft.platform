package com.arborsoft.platform.test;

import com.arborsoft.platform.config.ApplicationConfiguration;
import com.arborsoft.platform.domain.BaseNode;
import com.arborsoft.platform.domain.BaseRelationship;
import com.arborsoft.platform.service.Neo4jService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ApplicationConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MainTest {
    private static final Logger LOG = LoggerFactory.getLogger(MainTest.class);

    @Autowired
    private Neo4jService neo4j;

    @Test
    public void test1() throws Exception {
//        BaseNode node1 = new BaseNode()
//                .set("key1", "value1")
//                .set("key2", "value2")
//                .set("key3", "value3")
//                .set("key4", "value4")
//                .set("key5", "value5")
//                .addLabel("Label1")
//                .addLabel("Label2")
//                .addLabel("Label3")
//                .addLabel("Label4")
//                .addLabel("Label5")
//                ;
//
//        node1 = this.neo4j.save(node1);
//        LOG.info(node1.toString());

//        LOG.info(this.neo4j.get(0l).toString());

        BaseNode node1 = new BaseNode().set("key1", "value1");
        BaseNode node2 = new BaseNode().set("key2", "value2");

        node1 = this.neo4j.save(node1);
        node2 = this.neo4j.save(node2);

        BaseRelationship rel = new BaseRelationship(DynamicRelationshipType.withName("REL_TYPE")).set("key3", "value3");

        rel = this.neo4j.relate(node1, rel, node2);
        System.out.println(rel.toString());
    }
}
