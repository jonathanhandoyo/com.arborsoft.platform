package com.arborsoft.platform.controller;

import com.arborsoft.platform.service.Neo4jService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@Api(value = "Metadata Controller")
@RestController
@RequestMapping("/meta")
@ResponseBody
public class MetaController {
    @Autowired
    protected Neo4jService neo4j;

    @ApiOperation(
            value = "Get Labels Metadata",
            notes = "",
            response = String.class,
            responseContainer = "Set"
    )
    @RequestMapping(
            value = "/labels",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Set<String> labels() throws Exception {
        return this.neo4j.getLabels().stream().map(it -> it.name()).collect(Collectors.toSet());
    }

    @ApiOperation(
            value = "Get Relationships Metadata",
            notes = "",
            response = String.class,
            responseContainer = "Set"
    )
    @RequestMapping(
            value = "/relationships",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Set<String> relationships() throws Exception {
        return this.neo4j.getRelationshipTypes().stream().map(it -> it.name()).collect(Collectors.toSet());
    }
}
