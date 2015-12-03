package com.arborsoft.platform.controller;

import com.arborsoft.platform.domain.BaseNode;
import com.arborsoft.platform.exception.ObjectNotFoundException;
import com.arborsoft.platform.service.Neo4jService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static com.arborsoft.platform.util.CustomCollection.unwind;

@Api(value = "Node Controller")
@RestController
@RequestMapping("/nodes")
@ResponseBody
public class NodeController {
    private static final Logger LOG = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    protected Neo4jService neo4j;

    @ApiOperation(
            value = "Get Node(s) by Parameter",
            notes = "",
            response = BaseNode.class,
            responseContainer = "Set"
    )
    @RequestMapping(
            value = "/{filter}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Set<BaseNode> get(
            @ApiParam(name = "filter", required = true, value = "ID or Label")
            @PathVariable
            String filter,

            @ApiParam(name = "parameters", required = true, value = "Dynamic key-value pairs<br>Not testable via Swagger<br>Spring managed only")
            @RequestParam(required = false)
            HashMap<String, Object> parameters

    ) throws Exception {
        if (NumberUtils.isNumber(filter)) {

            Long id = NumberUtils.createLong(filter);
            BaseNode node = this.neo4j.get(id);
            if (node == null) throw new ObjectNotFoundException("id:" + id);

            return Collections.singletonList(node).stream().collect(Collectors.toSet());
        } else {

            Set<BaseNode> nodes = this.neo4j.get(filter, unwind(parameters));
            if (nodes == null || nodes.isEmpty()) throw new ObjectNotFoundException(new ObjectMapper().writer().writeValueAsString(parameters));
            return nodes;
        }
    }

    @ApiOperation(
            value = "Delete Node",
            notes = "",
            response = Void.class
    )
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    public void delete(
            @ApiParam(name = "id", required = true, value = "ID")
            @PathVariable
            Long id
    ) throws Exception {
        BaseNode node = this.neo4j.get(id);

        if (node == null) throw new ObjectNotFoundException("id:" + id);

        this.neo4j.delete(node);
    }
}
