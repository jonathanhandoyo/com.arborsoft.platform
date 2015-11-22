package com.arborsoft.platform.domain;

import com.arborsoft.platform.util.CustomComparator;
import com.arborsoft.platform.util.CustomTransformer;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.rest.graphdb.util.ResultConverter;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class BaseNode extends BaseDomain {
    protected Node node;
    protected Set<Label> labels = new HashSet<>();

    public BaseNode() {
        this.addLabel("BaseNode");
    }

    public BaseNode(Node node) {
        Assert.notNull(node, "Shadow node is null");

        this.with(node);
        this.addLabel("BaseNode");
    }

    public BaseNode addLabel(String label) {
        this.labels.add(DynamicLabel.label(label));
        return this;
    }

    public void with(Node node) {
        Assert.notNull(node, "Shadow node is null");

        this.node = node;
        this.labels = Sets.newHashSet(node.getLabels());

        this.set("__id__", node.getId());
        this.set("__class__", BaseNode.class.getName());
        this.set("__labels__", FluentIterable.from(node.getLabels())
                .transform(CustomTransformer.Label_String)
                .toSortedSet(CustomComparator.Ascending_String)
        );

        for (String key: node.getPropertyKeys()) {
            if (key.startsWith("__")) continue;
            this.set(key, node.getProperty(key));
        }
    }

    public BaseNode set(String key, Object value) {
        Assert.notNull(key, "Key is null");
        Assert.notNull(value, "Value is null");

        this.put(key, value);
        return this;
    }

    public static ResultConverter<Map<String, Object>, BaseNode> converter(final String identifier) {
        return new ResultConverter<Map<String, Object>, BaseNode>() {
            @Override
            public BaseNode convert(Map<String, Object> map, Class<BaseNode> aClass) {
                return new BaseNode((Node) map.get(identifier));
            }
        };
    }
}
