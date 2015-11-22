package com.arborsoft.platform.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.rest.graphdb.util.ResultConverter;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class BaseRelationship extends BaseDomain {
    protected Relationship relationship;
    protected RelationshipType type;

    @JsonInclude
    protected BaseDomain target;

    public BaseRelationship(RelationshipType type) {
        this.type = type;
    }

    public BaseRelationship(Relationship relationship, Node node) {
        Assert.notNull(relationship, "Shadow relationship is null");
        Assert.notNull(node, "Shadow node is null");

        this.with(relationship);
    }

    public void with(Relationship relationship) {
        Assert.notNull(relationship, "Shadow relationship is null");

        this.relationship = relationship;
        this.type = relationship.getType();
        this.target = new BaseNode(relationship.getEndNode());

        this.set("__id__", relationship.getId());
        this.set("__class__", BaseRelationship.class.getName());
        this.set("target", this.target);

        for (String key: relationship.getPropertyKeys()) {
            if (key.startsWith("__")) continue;
            this.set(key, relationship.getProperty(key));
        }
    }

    public BaseRelationship set(String key, Object value) {
        Assert.notNull(key, "Key is null");
        Assert.notNull(value, "Value is null");

        this.put(key, value);
        return this;
    }

    public static ResultConverter<Map<String, Object>, BaseRelationship> converter(final String relationshipIdentifier, final String nodeIdentifier) {
        return new ResultConverter<Map<String, Object>, BaseRelationship>() {
            @Override
            public BaseRelationship convert(Map<String, Object> map, Class<BaseRelationship> aClass) {
                return new BaseRelationship((Relationship) map.get(relationshipIdentifier), (Node) map.get(nodeIdentifier));
            }
        };
    }
}
