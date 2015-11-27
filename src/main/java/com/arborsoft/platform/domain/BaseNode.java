package com.arborsoft.platform.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.graphdb.Node;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@Setter
public class BaseNode extends BaseDomain {
    @JsonIgnore
    protected Node node;

    public BaseNode() {
        super.put("__labels__", new TreeSet<>());
        this.addLabel("BaseNode");
    }

    public BaseNode(Node node) {
        this.with(node);
    }

    public void with(Node node) {
        Assert.notNull(node, "Shadow node is null");

        super.put("__id__", node.getId());
        super.put("__labels__", new TreeSet<>(StreamSupport.stream(node.getLabels().spliterator(), false).map(it -> it.name()).collect(Collectors.toSet())));
        this.node = node;

        for (String key: this.node.getPropertyKeys()) {
            this.set(key, this.node.getProperty(key));
        }
    }

    public TreeSet<String> getLabels() {
        return ((TreeSet<String>) super.get("__labels__"));
    }

    public void addLabel(String label) {
        this.getLabels().add(label);
    }

    public static Function<Map<String, Object>, BaseNode> converter(final String identifier) {
        return (map) -> new BaseNode((Node) map.get(identifier));
    }
}
