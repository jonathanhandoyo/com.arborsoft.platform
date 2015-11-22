package com.arborsoft.platform.service;

import static com.arborsoft.platform.util.CustomCypher.toPropertyValues;
import static org.neo4j.cypherdsl.CypherQuery.*;

import com.arborsoft.platform.domain.BaseNode;
import com.arborsoft.platform.domain.BaseRelationship;
import com.arborsoft.platform.exception.DatabaseOperationException;
import com.arborsoft.platform.util.CustomTransformer;
import com.google.common.collect.FluentIterable;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.grammar.Execute;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Service
public class Neo4jService {
    private static final Logger LOG = LoggerFactory.getLogger(Neo4jService.class);

    @Autowired
    private RestGraphDatabase database;

    @Autowired
    private RestCypherQueryEngine engine;

    public FluentIterable<String> labels() {
        return FluentIterable.from(this.database.getAllLabelNames());
    }

    public FluentIterable<String> relationships() {
        return FluentIterable.from(this.database.getRelationshipTypes()).transform(CustomTransformer.RelationshipType_String);
    }

    public BaseNode save(BaseNode node) throws DatabaseOperationException {
        try {
            Assert.notNull(node, "BaseNode is null");

            Node _node = (node.get("__id__") != null ? this.database.getNodeById((Long) node.get("__id__")) : null);
            if (_node == null) {
                _node = this.database.createNode(node.getLabels().toArray(new Label[node.getLabels().size()]));
                Assert.state(_node != null, "Unable to create node");
            }

            for (String key: _node.getPropertyKeys()) {
                _node.removeProperty(key);
            }

            for (String key: node.keySet()) {
                if (key.startsWith("__")) continue;
                _node.setProperty(key, node.get(key));
            }

            node.with(_node);

            return node;
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public void delete(BaseNode node) throws DatabaseOperationException {
        try {
            Assert.notNull(node, "BaseNode is null");
            Assert.notNull(node.get("__id__"), "Node.id is null");

            Node _node = this.database.getNodeById((Long) node.get("__id__"));
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
                            nodesById("target", target.getNode().getId())
                    ).createUnique(
                            node("origin").out(relationship.getType().name()).values(toPropertyValues(param, relationship)).as("relationship").node("target")
                    )
                    .returns(
                            identifier("relationship"),
                            identifier("target")
                    )
                    ;

            return FluentIterable.from(this.engine.query(query.toString(), param).to(BaseRelationship.class, BaseRelationship.converter("relationship", "target"))).first().orNull();
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public BaseRelationship unrelate(BaseRelationship relationship) throws DatabaseOperationException {
        try {
            Assert.notNull(relationship, "Relationship is null");
            Assert.notNull(relationship.getRelationship(), "Relationship is null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery
                    .start(relationshipsById("relationship", relationship.getRelationship().getId()))
                    .delete(identifier("relationship"))
                    ;

            return FluentIterable.from(this.engine.query(query.toString(), param).to(BaseRelationship.class, BaseRelationship.converter("relationship", "target"))).first().orNull();
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

    public FluentIterable<BaseNode> get(String label, Pair<String, Object>... pairs) throws DatabaseOperationException {
        try {
            Assert.notNull(label, "Label is null");
            Assert.notEmpty(pairs, "Properties are null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery
                    .match(node("node").label(label).values(toPropertyValues(param, pairs)))
                    .returns(identifier("node"));

            return FluentIterable.from(this.engine.query(query.toString(), param).to(BaseNode.class, BaseNode.converter("node")));
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }
}
