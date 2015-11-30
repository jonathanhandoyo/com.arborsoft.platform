package com.arborsoft.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.graphdb.Node;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
@NoArgsConstructor
public class Metadata {
    protected Long id;
    protected String label;
    protected String type;
    protected String[] keys;

    public Metadata(Node node) {
        Assert.notNull(node, "Shadow node is null");

        this.setId(node.getId());
        this.setLabel((String) node.getProperty("label", null));
        this.setType((String) node.getProperty("type", null));
        this.setKeys((String[]) node.getProperty("keys", null));
    }

    public static Function<Map<String, Object>, Metadata> converter(final String identifier) {
        return (map) -> new Metadata((Node) map.get(identifier));
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseDomain _o = (BaseDomain) o;
        if (this.getId() == null) return super.equals(o);
        return this.getId().equals(_o.getId());
    }

    @Override
    public int hashCode() {
        return this.getId() != null ? this.getId().hashCode() : super.hashCode();
    }
}
