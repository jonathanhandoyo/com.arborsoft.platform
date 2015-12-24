package com.arborsoft.platform.api.controller;

import com.arborsoft.platform.api.config.ApiConfiguration;
import com.arborsoft.platform.api.config.TestApiConfiguration;
import com.arborsoft.platform.core.domain.BaseNode;
import com.arborsoft.platform.core.service.Neo4jService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
        loader = SpringApplicationContextLoader.class,
        classes = {
                ApiConfiguration.class,
                TestApiConfiguration.class
        }
)
public class NodeControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private NodeController nodeController;

    @Before
    public void setup() {}

    @After
    public void tearDown() {}

    @Test
    public void testGet() throws Exception {
//        when(this.nodeController.get(1L)).thenReturn(ResponseEntity.ok(new BaseNode("Test")));
        ResponseEntity responseEntity = this.nodeController.get(1L);
        assertTrue(true);
    }

    @Test
    public void testGetByCriteria() throws Exception {

    }

    @Test
    public void testGetKeys() throws Exception {

    }

    @Test
    public void testGetLabels() throws Exception {

    }

    @Test
    public void testGetRelationships() throws Exception {

    }

    @Test
    public void testGetRelationships1() throws Exception {

    }
}