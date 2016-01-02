package com.arborsoft.platform.api.controller;

import com.arborsoft.platform.core.domain.BaseNode;
import com.arborsoft.platform.core.domain.BaseRelationship;
import com.arborsoft.platform.core.dto.RelationshipDTO;
import com.arborsoft.platform.core.service.Neo4jService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.DynamicLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.arborsoft.platform.core.util.CustomCollection.unwind;

@RestController
@RequestMapping("/rest")
@ResponseBody
public class NodeController {

    @Autowired
    protected Neo4jService neo4j;

    @ApiOperation(value = "Get Node by ID")
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> get(@PathVariable Long id) throws Exception {
        return new ResponseEntity<>(this.neo4j.get(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Update Node by ID")
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BaseNode> put(@PathVariable Long id, @RequestBody Map<String, Object> body) throws Exception {
        BaseNode node = this.neo4j.get(id);
        if (node == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        this.copy(node, body, false);

        this.neo4j.save(node);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @ApiOperation(value = "Create Node with Label")
    @RequestMapping(
            value = "/:{label}",
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
                node = new BaseNode(label);
                body.remove("__id__");
            }
        }

        node = (node == null) ? new BaseNode(label) : node;
        node = this.copy(node, body, true);

        node.addLabel(label);
        this.neo4j.save(node);
        return new ResponseEntity<>(node, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete Node by ID")
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) throws Exception {
        BaseNode node = this.neo4j.get(id);
        if (node == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        this.neo4j.delete(node);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Delete Nodes by Label")
    @RequestMapping(
            value = "/:{label}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<Void> delete(@PathVariable String label) throws Exception {
        this.neo4j.delete(DynamicLabel.label(label));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Get Nodes by Label & Criteria", responseContainer = "Set")
    @RequestMapping(
            value = "/:{label}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Set<BaseNode>> getByCriteria(@PathVariable String label, @RequestBody(required = false) Map<String, Object> criteria) throws Exception {
        Set<BaseNode> nodes = this.neo4j.get(label, unwind(criteria));
        return new ResponseEntity<>(nodes, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Keys by Label", responseContainer = "Set")
    @RequestMapping(
            value = "/:{label}/keys",
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
        Set<String> labels = this.neo4j.getLabels().stream().collect(Collectors.toSet());
        return new ResponseEntity<>(labels, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Relationships", responseContainer = "Map")
    @RequestMapping(
            value = "/{id}/rels",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Set<RelationshipDTO>> getRelationships(@PathVariable Long id) throws Exception {
        BaseNode node = this.neo4j.get(id);
        if (node == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(Stream.concat(
                this.neo4j.getIncomingRelationshipTypes(node).stream(),
                this.neo4j.getOutgoingRelationshipTypes(node).stream()
        ).sorted(RelationshipDTO::compareTo).collect(Collectors.toSet()), HttpStatus.OK);
    }

    @ApiOperation(value = "Traverse Relationship", responseContainer = "Set")
    @RequestMapping(
            value = "/{id}/{direction}:{type}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Set<BaseRelationship>> getRelationships(@PathVariable Long id, @PathVariable RelationshipDTO.Direction direction, @PathVariable String type, @RequestParam(required = false) String json) throws Exception {
        BaseNode node = this.neo4j.get(id);
        if (node == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Map<String, Object> map = null;
        if (StringUtils.isNotBlank(json)) {
            map = new ObjectMapper().readerFor(new TypeReference<Map<String, Object>>() {}).readValue(json);
        }

        Set<BaseRelationship> result;
        switch (direction) {
            case IN: {
                result = this.neo4j.getIncoming(node, type, unwind(map));
                return result.isEmpty() ? ResponseEntity.ok(null) : ResponseEntity.ok(result);
            }
            case OUT: {
                result = this.neo4j.getIncoming(node, type, unwind(map));
                return result.isEmpty() ? ResponseEntity.ok(null) : ResponseEntity.ok(result);
            }
            default: {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
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