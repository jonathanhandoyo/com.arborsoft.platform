package com.arborsoft.platform.util;

import org.neo4j.graphdb.Label;

import java.util.Comparator;

public final class CustomComparator {
    public static final Comparator<String> Ascending_String = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    public static final Comparator<Label> Ascending_Label = new Comparator<Label>() {
        @Override
        public int compare(Label o1, Label o2) {
            return o1.name().compareTo(o2.name());
        }
    };
}
