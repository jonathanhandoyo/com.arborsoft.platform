package com.arborsoft.platform.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationshipDTO implements Comparable<RelationshipDTO> {

    protected Direction direction;
    protected String type;
    protected String label;

    public RelationshipDTO(Direction direction, Map<String, Object> map) {
        this.direction = direction;
        this.type = (String) map.get("type");
        this.label = (String) map.get("label");
    }

    @Override
    public int compareTo(RelationshipDTO other) {
        final int BEFORE = -1;
        final int EQUAL  = 0;
        final int AFTER  = +1;

        if (this.direction.ordinal() < other.direction.ordinal()) return BEFORE;
        if (this.direction.ordinal() > other.direction.ordinal()) return AFTER;

        int result;
        result = this.getType().compareTo(other.getType());     if (result != EQUAL) return result;
        result = this.getLabel().compareTo(other.getLabel());   if (result != EQUAL) return result;
        return result;
    }

    public enum Direction {
        IN,
        OUT,
        ;
    }
}
