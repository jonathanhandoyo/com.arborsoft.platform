package com.arborsoft.platform.api.controller;

import com.arborsoft.platform.core.domain.BaseNode;
import com.arborsoft.platform.core.domain.BaseRelationship;
import com.arborsoft.platform.core.service.Neo4jService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.neo4j.graphdb.DynamicLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.arborsoft.platform.core.util.CustomCollection.unwind;

@Api(value = "Node Controller")
@RestController
@RequestMapping("/rest")
@ResponseBody
public class NodeController {

    public enum Direction {
        IN, OUT;
    }

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
            value = "/label:{label}",
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
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        BaseNode node = this.neo4j.get(id);

        this.neo4j.delete(node);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Delete Nodes with Label")
    @RequestMapping(
            value = "/label:{label}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(@PathVariable String label) throws Exception {
        this.neo4j.delete(DynamicLabel.label(label));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Get Nodes by Criteria", responseContainer = "Set")
    @RequestMapping(
            value = "/label:{label}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Set<BaseNode>> getByCriteria(@PathVariable String label, @RequestParam(required = false) String json) throws Exception {
        Map<String, Object> map = new ObjectMapper().readerFor(new TypeReference<Map<String, Object>>() {}).readValue(json);

        Set<BaseNode> nodes = this.neo4j.get(label, unwind(map));
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Keys by Label", responseContainer = "Set")
    @RequestMapping(
            value = "/label:{label}/keys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Set<String>> getKeys(@PathVariable String label) throws Exception {
        Set<String> keys = this.neo4j.getKeys(DynamicLabel.label(label));
        return new ResponseEntity<>(keys, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Labels", responseContainer = "Set")
    @RequestMapping(
            value = "/labels",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Set<String>> getLabels() throws Exception {
        Set<String> labels = this.neo4j.getLabels().stream().map(it -> it.name()).collect(Collectors.toSet());
        return new ResponseEntity<>(labels, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Relationships", responseContainer = "Map")
    @RequestMapping(
            value = "/id:{id}/rels",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Set<String>>> getRelationships(@PathVariable Long id) throws Exception {
        BaseNode node = this.neo4j.get(id);

        Map<String, Set<String>> result = new HashMap<>();
        result.put("IN", this.neo4j.getIncomingRelationshipTypes(node));
        result.put("OUT", this.neo4j.getOutgoingRelationshipTypes(node));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(value = "Traverse Relationship", responseContainer = "Set")
    @RequestMapping(
            value = "/id:{id}/{direction}:{type}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Set<BaseRelationship>> getRelationships(@PathVariable Long id, @PathVariable Direction direction, @PathVariable String type, @RequestParam(required = false) String json) throws Exception {
        BaseNode node = this.neo4j.get(id);

        Map<String, Object> map = new ObjectMapper().readerFor(new TypeReference<Map<String, Object>>() {}).readValue(json);

        if (Direction.OUT.equals(direction)) return new ResponseEntity<>(this.neo4j.getOutgoing(node, type, unwind(map)), HttpStatus.OK);
        if (Direction.IN.equals(direction)) return new ResponseEntity<>(this.neo4j.getIncoming(node, type, unwind(map)), HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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