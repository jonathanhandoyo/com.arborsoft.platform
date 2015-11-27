package com.arborsoft.platform.controller;

import com.arborsoft.platform.domain.BaseNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.arborsoft.platform.service.Neo4jService;

@Controller
@RequestMapping("/test")
@ResponseBody
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Autowired
    protected Neo4jService neo4j;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseNode get(@PathVariable Long id) throws Exception {
        return this.neo4j.get(id);
    }
}
