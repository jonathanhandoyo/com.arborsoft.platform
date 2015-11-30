package com.arborsoft.platform.controller;

import com.arborsoft.platform.domain.BaseNode;
import com.arborsoft.platform.exception.ObjectNotFoundException;
import com.arborsoft.platform.service.Neo4jService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;

import static com.arborsoft.platform.util.CustomCollection.unwind;

@Api(value = "Node Controller")
@RestController
@RequestMapping("/nodes")
@ResponseBody
public class NodeController {
    private static final Logger LOG = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    protected Neo4jService neo4j;

    @ApiOperation(value = "Get Node by ID", notes = "")
    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public BaseNode get(
            @ApiParam(name = "id")
            @RequestParam
            Long id
    ) throws Exception {
        BaseNode node = this.neo4j.get(id);

        if (node == null) throw new ObjectNotFoundException("id:" + id);

        return node;
    }

    @ApiOperation(value = "Get Node(s) by Parameter", notes = "")
    @RequestMapping(
            value = "/{label}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Set<BaseNode> get(
            @ApiParam(name = "label", required = false, value = "Label")
            @PathVariable
            String label,

            @ApiParam(name = "parameters", required = false, value = "Dynamic key-value pairs<br>Not testable via Swagger<br>Spring managed only")
            @RequestParam(required = false)
            HashMap<String, Object> parameters

    ) throws Exception {
        Set<BaseNode> nodes = this.neo4j.get(label, unwind(parameters));

        if (nodes == null || nodes.isEmpty()) throw new ObjectNotFoundException(new ObjectMapper().writer().writeValueAsString(parameters));

        return nodes;
    }
}
