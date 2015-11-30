package com.arborsoft.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.graphdb.Relationship;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
public class BaseRelationship extends BaseDomain {
    @JsonIgnore
    protected Relationship relationship;

    public BaseRelationship(String relationshipType) {
        super.put("__type__", relationshipType);
    }

    public BaseRelationship(Relationship relationship) {
        Assert.notNull(relationship, "Shadow relationship is null");
        Assert.notNull(relationship.getEndNode(), "Target node is null");

        super.put("__id__", relationship.getId());
        super.put("__type__", relationship.getType());
        super.put("__origin__", relationship.getStartNode().getId());
        super.put("__target__", relationship.getEndNode().getId());
        this.relationship = relationship;

        for (String key: this.relationship.getPropertyKeys()) {
            if (key.startsWith("__")) continue;
            super.put(key, this.relationship.getProperty(key));
        }
    }

    public String getType() {
        return (String) super.get("__type__");
    }

    public static Function<Map<String, Object>, BaseRelationship> converter(final String identifier) {
        return (map) -> new BaseRelationship((Relationship) map.get(identifier));
    }
}
