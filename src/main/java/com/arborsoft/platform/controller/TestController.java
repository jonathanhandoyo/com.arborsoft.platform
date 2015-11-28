package com.arborsoft.platform.controller;

import com.arborsoft.platform.domain.BaseNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.arborsoft.platform.service.Neo4jService;

@Api(value = "Test Controller")
@RestController
@RequestMapping("/test")
@ResponseBody
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @Autowired
    protected Neo4jService neo4j;

    @ApiOperation(value = "value", notes = "notes")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseNode get(@ApiParam(name = "id") @PathVariable Long id) throws Exception {
        return this.neo4j.get(id);
    }
}
