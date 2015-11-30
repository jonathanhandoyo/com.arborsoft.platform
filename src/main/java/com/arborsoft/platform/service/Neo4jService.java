package com.arborsoft.platform.service;

import com.arborsoft.platform.domain.BaseNode;
import com.arborsoft.platform.domain.BaseRelationship;
import com.arborsoft.platform.domain.Metadata;
import com.arborsoft.platform.exception.DatabaseOperationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.grammar.Execute;
import org.neo4j.graphdb.*;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.arborsoft.platform.util.CustomCypher.parameter;
import static org.apache.commons.lang3.tuple.Pair.*;
import static org.neo4j.cypherdsl.CypherQuery.*;
import static com.arborsoft.platform.util.CustomCypher.toPropertyValues;

@Service
public class Neo4jService {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jService.class);

    @Autowired
    private RestGraphDatabase database;

    @Autowired
    private RestCypherQueryEngine engine;

    public void clear() throws DatabaseOperationException {
        try {
            this.engine.query("MATCH (n) WITH n OPTIONAL MATCH (n) -[r]- () DELETE n;", null);
            this.engine.query("MATCH (n) DELETE n;", null);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<Label> labels() {
        return this.database.getAllLabelNames().stream().map(it -> DynamicLabel.label(it)).collect(Collectors.toSet());
    }

    public Set<RelationshipType> relationships() {
        return StreamSupport.stream(this.database.getRelationshipTypes().spliterator(), false).collect(Collectors.toSet());
    }

    public Set<Metadata> getAllMetadata() throws DatabaseOperationException {
        try {
            Execute query = CypherQuery
                    .match(node("metadata").label("Metadata"))
                    .returns(identifier("metadata"));

            return StreamSupport.stream(this.engine.query(query.toString(), null).spliterator(), false).map(Metadata.converter("metadata")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Metadata save(Metadata metadata) throws DatabaseOperationException {
        try {
            Assert.notNull(metadata, "Metadata is null");

            Node _node = (metadata.getId() != null ? this.database.getNodeById(metadata.getId()) : null);
            if (_node == null) {
                _node = this.database.createNode(DynamicLabel.label("Metadata"));
                Assert.notNull(_node, "Unable to create node");
                Assert.notNull(_node.getId(), "Unable to create node");
            }

            _node.setProperty("label", metadata.getLabel());
            _node.setProperty("keys", metadata.getKeys());

            metadata.setId(_node.getId());

            return metadata;
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Metadata register(Label label, String... keys) throws DatabaseOperationException {
        try {
            return this.register(label, Arrays.asList(keys).stream().collect(Collectors.toSet()));
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Metadata register(Label label, Set<String> keys) throws DatabaseOperationException {
        try {
            Assert.notEmpty(keys, "No key(s)");

            Metadata metadata = this.getMetadata(label);

            if (metadata == null) {
                metadata = new Metadata();
                metadata.setLabel(label.name());
                metadata.setKeys(keys.stream().filter(it -> !it.startsWith("__")).toArray(String[]::new));
            } else {
                Set<String> distinct = Arrays.asList(metadata.getKeys()).stream().collect(Collectors.toSet());
                distinct.addAll(keys.stream().filter(it -> !it.startsWith("__")).collect(Collectors.toSet()));

                metadata.setKeys(distinct.toArray(new String[distinct.size()]));
            }

            return this.save(metadata);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Metadata register(RelationshipType type, String... keys) throws DatabaseOperationException {
        try {
            return this.register(type, Arrays.asList(keys).stream().collect(Collectors.toSet()));
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Metadata register(RelationshipType type, Set<String> keys) throws DatabaseOperationException {
        try {
            Assert.notEmpty(keys, "No key(s)");

            Metadata metadata = this.getMetadata(type);

            if (metadata == null) {
                metadata = new Metadata();
                metadata.setType(type.name());
                metadata.setKeys(keys.stream().filter(it -> !it.startsWith("__")).toArray(String[]::new));
            } else {
                Set<String> distinct = Arrays.asList(metadata.getKeys()).stream().collect(Collectors.toSet());
                distinct.addAll(keys.stream().filter(it -> !it.startsWith("__")).collect(Collectors.toSet()));

                metadata.setKeys(distinct.toArray(new String[distinct.size()]));
            }

            return this.save(metadata);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Metadata getMetadata(Label label) throws DatabaseOperationException {
        try {
            Assert.notNull(label, "Label is null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery
                    .match(node("metadata").label("Metadata"))
                    .where(identifier("metadata").property("label").eq(parameter(param, of("label", label.name()))))
                    .returns(identifier("metadata"));

            return StreamSupport.stream(this.engine.query(query.toString(), param).spliterator(), false).map(Metadata.converter("metadata")).findFirst().orElse(null);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Metadata getMetadata(RelationshipType type) throws DatabaseOperationException {
        try {
            Assert.notNull(type, "RelationshipType is null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery
                    .match(node("metadata").label("Metadata"))
                    .where(identifier("metadata").property("type").eq(parameter(param, of("type", type.name()))))
                    .returns(identifier("metadata"));

            return StreamSupport.stream(this.engine.query(query.toString(), param).spliterator(), false).map(Metadata.converter("metadata")).findFirst().orElse(null);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public BaseNode save(BaseNode node) throws DatabaseOperationException {
        try {
            Assert.notNull(node, "BaseNode is null");

            Node _node = (node.getId() != null ? this.database.getNodeById(node.getId()) : null);
            if (_node == null) {
                _node = this.database.createNode(node.getLabels().stream().map(it -> DynamicLabel.label(it)).toArray(Label[]::new));
                Assert.notNull(_node, "Unable to create node");
                Assert.notNull(_node.getId(), "Unable to create node");
            }

            for (String key: _node.getPropertyKeys()) {
                _node.removeProperty(key);
            }

            for (String key: node.keySet()) {
                _node.setProperty(key, node.get(key));
            }

            node.setNode(_node);
            node.setId(_node.getId());

            return node;
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public void delete(BaseNode node) throws DatabaseOperationException {
        try {
            Assert.notNull(node, "BaseNode is null");
            Assert.notNull(node.getId(), "Node.id is null");

            Node _node = this.database.getNodeById(node.getId());
            if (_node.hasRelationship()) {
                for (Relationship relationship: _node.getRelationships()) {
                    relationship.delete();
                }
                _node.delete();
            }
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public BaseRelationship relate(BaseNode origin, BaseRelationship relationship, BaseNode target) throws DatabaseOperationException {
        try {
            Assert.notNull(origin, "Origin BaseNode is null");
            Assert.notNull(target, "Target BaseNode is null");
            Assert.notNull(relationship, "Relationship is null");
            Assert.notNull(relationship.getType(), "RelationshipType is null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery
                    .start(
                            nodesById("origin", origin.getNode().getId()),
                            nodesById("target", target.getNode().getId()))
                    .createUnique(
                            node("origin").out(relationship.getType()).values(toPropertyValues(param, relationship)).as("relationship").node("target"))
                    .returns(identifier("relationship"))
                    ;

            return StreamSupport.stream(this.engine.query(query.toString(), param).spliterator(), false).findFirst().map(BaseRelationship.converter("relationship")).orElse(null);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public void unrelate(BaseRelationship relationship) throws DatabaseOperationException {
        try {
            Assert.notNull(relationship, "Relationship is null");
            Assert.notNull(relationship.getId(), "Relationship.id is null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery
                    .start(relationshipsById("relationship", relationship.getId()))
                    .delete(identifier("relationship"))
                    ;

            this.engine.query(query.toString(), param);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public BaseNode get(Long id) throws DatabaseOperationException {
        try {
            Assert.notNull(id, "ID is null");
            return new BaseNode(this.database.getNodeById(id));
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<BaseNode> get(String label, Pair<String, Object>... pairs) throws DatabaseOperationException {
        try {
            Assert.notNull(label, "Label is null");
            Assert.notEmpty(pairs, "Properties are null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery
                    .match(node("node").label(label).values(toPropertyValues(param, pairs)))
                    .returns(identifier("node"));

            return StreamSupport.stream(this.engine.query(query.toString(), param).spliterator(), false).map(BaseNode.converter("node")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }
}
