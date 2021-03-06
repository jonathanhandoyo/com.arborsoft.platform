package com.arborsoft.platform.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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

    public BaseNode(String label) {
        Assert.state(StringUtils.isNotBlank(label));

        super.put("__labels__", new TreeSet<>());
        this.addLabel(label);
    }

    public BaseNode(Node node) {
        Assert.notNull(node, "Shadow node is null");

        super.put("__id__", node.getId());
        super.put("__labels__", new TreeSet<>(StreamSupport.stream(node.getLabels().spliterator(), false).map(it -> it.name()).collect(Collectors.toSet())));
        this.node = node;

        for (String key: this.node.getPropertyKeys()) {
            if (key.startsWith("__")) continue;
            super.set(key, this.node.getProperty(key));
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
