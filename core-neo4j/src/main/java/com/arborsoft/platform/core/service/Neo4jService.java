package com.arborsoft.platform.core.service;

import com.arborsoft.platform.core.domain.BaseNode;
import com.arborsoft.platform.core.domain.BaseRelationship;
import com.arborsoft.platform.core.dto.RelationshipDTO;
import com.arborsoft.platform.core.exception.DatabaseOperationException;
import com.arborsoft.platform.core.util.CustomMap;
import com.arborsoft.platform.core.util.CustomStream;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.cypherdsl.CypherQuery;
import org.neo4j.cypherdsl.grammar.Execute;
import org.neo4j.cypherdsl.grammar.Match;
import org.neo4j.cypherdsl.grammar.Return;
import org.neo4j.graphdb.*;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.arborsoft.platform.core.util.CustomCypher.toPropertyValues;
import static com.arborsoft.platform.core.util.CustomMap.*;
import static com.arborsoft.platform.core.util.CustomMap.entry;
import static org.neo4j.cypherdsl.CypherQuery.*;

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

    public BaseNode save(BaseNode node) throws DatabaseOperationException {
        try {
            Assert.notNull(node, "BaseNode is null");

            Node _node = (node.getId() != null ? this.database.getNodeById(node.getId()) : null);
            if (_node == null) {
                _node = this.database.createNode(node.getLabels().stream().map(DynamicLabel::label).toArray(Label[]::new));
                Assert.notNull(_node, "Unable to create node");
                Assert.notNull(_node.getId(), "Unable to create node");
            }

            Set<String> existings = new HashSet<>();
            _node.getPropertyKeys().forEach(existings::add);

            for (String key: node.keySet()) {
                _node.setProperty(key, node.get(key));
                existings.remove(key);
            }

            for (String key: existings) {
                _node.removeProperty(key);
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

    public void delete(Label label) throws DatabaseOperationException {
        try {
            Assert.notNull(label, "Label is null");

            this.engine.query("MATCH (n:" + label.name() + ") WITH n OPTIONAL MATCH (n) -[r]- () DELETE r, n;", null);
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


            return CustomStream.stream(this.engine.query(query.toString(), param)).findFirst().map(BaseRelationship.converter("relationship")).orElse(null);
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public void unrelate(BaseRelationship relationship) throws DatabaseOperationException {
        try {
            Assert.notNull(relationship, "Relationship is null");
            Assert.notNull(relationship.getId(), "Relationship.id is null");

            String query =
                    " START r = rel({id}) " +
                    "DELETE r;";

            this.engine.query(query, map(entry("id", relationship.getId())));
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
//            Assert.notEmpty(pairs, "Properties are null");

            Map<String, Object> param = new HashMap<>();

            Execute query;

            if (pairs != null && pairs.length > 0) {
                query = CypherQuery.match(node("node").label(label).values(toPropertyValues(param, pairs)));
            } else {
                query = CypherQuery.match(node("node").label(label));
            }

            ((Return) query).returns(identifier("node"));

            return CustomStream.stream(this.engine.query(query.toString(), param)).map(BaseNode.converter("node")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<String> getLabels() throws DatabaseOperationException {
        try {
            String query =
                    " MATCH (n) " +
                    "  WITH DISTINCT labels(n) AS labels " +
                    "UNWIND labels AS label " +
                    "RETURN DISTINCT label " +
                    " ORDER BY label;";
            return CustomStream.stream(this.engine.query(query, null)).map(it -> (String) it.get("label")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<String> getRelationshipTypes() throws DatabaseOperationException {
        try {
            String query =
                    " MATCH () -[r]- () " +
                    "RETURN DISTINCT type(r) AS relationship " +
                    " ORDER BY relationship;";
            return CustomStream.stream(this.engine.query(query, null)).map(it -> (String) it.get("relationship")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<String> getKeys(Label label) throws DatabaseOperationException {
        try {
            Assert.notNull(label, "Label is null");

            String query =
                    "  MATCH (n:" + label.name() + ") " +
                    " UNWIND keys(n) AS key " +
                    "   WITH key " +
                    "  WHERE NOT key =~ \"__.*\" " +
                    " RETURN DISTINCT key;";
            return CustomStream.stream(this.engine.query(query, null)).map(it -> (String) it.get("key")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<String> getKeys(RelationshipType type) throws DatabaseOperationException {
        try {
            Assert.notNull(type, "RelationshipType is null");

            String query =
                    "  MATCH () -[r:" + type.name() + "]- () " +
                    " UNWIND keys(r) AS key " +
                    "   WITH key " +
                    "  WHERE NOT key =~ \"__.*\" " +
                    " RETURN DISTINCT key;";
            return CustomStream.stream(this.engine.query(query, null)).map(it -> (String) it.get("key")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<RelationshipDTO> getOutgoingRelationshipTypes(BaseNode node) throws DatabaseOperationException {
        try {
            Assert.notNull(node, "BaseNode is null");
            Assert.notNull(node.getId(), "BaseNode.id is null");

            String query =
                    " START n = node({id}) " +
                    " MATCH (n) -[r]-> (m) " +
                    "UNWIND labels(m) AS label " +
                    "  WITH type(r) AS type, label " +
                    " WHERE label <> 'BaseNode' " +
                    "RETURN DISTINCT type, label;";

            return CustomStream.stream(this.engine.query(query, map(entry("id", node.getId())))).map(it -> new RelationshipDTO(RelationshipDTO.Direction.OUT, it)).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<RelationshipDTO> getIncomingRelationshipTypes(BaseNode node) throws DatabaseOperationException {
        try {
            Assert.notNull(node, "BaseNode is null");
            Assert.notNull(node.getId(), "BaseNode.id is null");

            String query =
                    " START n = node({id}) " +
                    " MATCH (n) <-[r]- (m) " +
                    "UNWIND labels(m) AS label " +
                    "  WITH type(r) AS type, label " +
                    " WHERE label <> \"BaseNode\" " +
                    "RETURN DISTINCT type, label;";

            return CustomStream.stream(this.engine.query(query, map(entry("id", node.getId())))).map(it -> new RelationshipDTO(RelationshipDTO.Direction.IN, it)).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<BaseRelationship> getOutgoing(BaseNode origin, String relationship, Pair<String, Object>... pairs) throws DatabaseOperationException {
        try {
            Assert.notNull(origin, "Origin BaseNode is null");
            Assert.notNull(relationship, "Relationship is null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery.start(nodesById("origin", origin.getNode().getId()));

            if (pairs != null && pairs.length > 0) {
                ((Match) query).match(node("origin").out(relationship).values(toPropertyValues(param, pairs)).as("relationship").node("target"));
            } else {
                ((Match) query).match(node("origin").out(relationship).as("relationship").node("target"));
            }

            ((Return) query).returns(identifier("relationship"));

            return CustomStream.stream(this.engine.query(query.toString(), null)).map(BaseRelationship.converter("relationship")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }

    public Set<BaseRelationship> getIncoming(BaseNode origin, String relationship, Pair<String, Object>... pairs) throws DatabaseOperationException {
        try {
            Assert.notNull(origin, "Origin BaseNode is null");
            Assert.notNull(relationship, "Relationship is null");

            Map<String, Object> param = new HashMap<>();

            Execute query = CypherQuery
                    .start(nodesById("origin", origin.getNode().getId()))
                    .match(node("origin").in(relationship).values(toPropertyValues(param, pairs)).as("relationship").node("target"))
                    .returns(identifier("relationship"))
                    ;

            return CustomStream.stream(this.engine.query(query.toString(), null)).map(BaseRelationship.converter("relationship")).collect(Collectors.toSet());
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            throw new DatabaseOperationException(exception.getMessage(), exception);
        }
    }
}
