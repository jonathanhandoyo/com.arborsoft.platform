package com.arborsoft.platform.controller;

import com.arborsoft.platform.domain.BaseNode;
import com.arborsoft.platform.service.Neo4jService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

import static com.arborsoft.platform.util.CustomCollection.unwind;

@Api(value = "Node Controller")
@RestController
@RequestMapping("/rest")
@ResponseBody
public class NodeController {

    @Autowired
    protected Neo4jService neo4j;

    @ApiOperation(value = "Get Node by ID")
    @RequestMapping(
            value = "/id:{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseNode> get(@PathVariable Long id) throws Exception {
        BaseNode node = this.neo4j.get(id);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @ApiOperation(value = "Update Node by ID")
    @RequestMapping(
            value = "/id:{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseNode> put(@PathVariable Long id, @RequestBody Map<String, Object> body) throws Exception {
        BaseNode node = this.neo4j.get(id);
        if (node == null) return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

        this.copy(node, body, false);

        this.neo4j.save(node);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @ApiOperation(value = "Create Node with Label")
    @RequestMapping(
            value = "/{label}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseNode> post(@PathVariable String label, @RequestBody Map<String, Object> body) throws Exception {
        Assert.notEmpty(body, "Request Body is empty");

        BaseNode node = null;
        if (body.containsKey("__id__")) {
            Long id = (Long) body.get("__id__");
            node = this.neo4j.get(id);

            if (node == null) {
                node = new BaseNode();
                body.remove("__id__");
            }
        }

        node = (node == null) ? new BaseNode() : node;
        node = this.copy(node, body, true);

        node.addLabel(label);
        this.neo4j.save(node);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete Node by ID")
    @RequestMapping(
            value = "/id:{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        BaseNode node = this.neo4j.get(id);

        this.neo4j.delete(node);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Get Node(s) by Parameter", responseContainer = "Set")
    @RequestMapping(
            value = "/{label}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> find(@PathVariable String label, @RequestParam String json) throws Exception {
        Map<String, Object> map = new ObjectMapper().readerFor(new TypeReference<Map<String, Object>>() {}).readValue(json);

        Set<BaseNode> nodes = this.neo4j.get(label, unwind(map));
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }

    private BaseNode copy(BaseNode node, Map<String, Object> map, boolean wipe) {
        if (wipe) {
            for (String key: node.keySet()) {
                if (key.startsWith("__")) continue;
                node.remove(key);
            }
        }

        for (String key: map.keySet()) {
            node.set(key, map.get(key));
        }

        return node;
    }
}