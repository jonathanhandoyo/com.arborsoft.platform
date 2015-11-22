package com.arborsoft.platform.util;

import com.google.common.base.Function;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public final class CustomTransformer {
    public static final Function<Label, String> Label_String = new Function<Label, String>() {
        @Override
        public String apply(Label label) {
            return label.name();
        }
    };

    public static final Function<RelationshipType, String> RelationshipType_String = new Function<RelationshipType, String>() {
        @Override
        public String apply(RelationshipType label) {
            return label.name();
        }
    };
}
