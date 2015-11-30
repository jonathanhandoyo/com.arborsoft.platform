package com.arborsoft.platform.util;

import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.cypherdsl.expression.Expression;
import org.neo4j.cypherdsl.query.PropertyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.cypherdsl.CypherQuery.param;
import static org.neo4j.cypherdsl.CypherQuery.value;

public class CustomCypher {
    public static Expression parameter(Map<String, Object> param, Pair<String, Object> pair) {
        if (pair.getKey() != null && pair.getValue() != null) {
            param.put(pair.getKey(), pair.getValue());
            return param(pair.getKey());
        }

        return null;
    }

    public static PropertyValue[] toPropertyValues(Map<String, Object> param, Pair<String, Object>... pairs) {
        if (pairs.length > 0) {
            List<PropertyValue> values = new ArrayList<>();
            for (Pair<String, Object> pair: pairs) {
                values.add(value(pair.getKey(), param(pair.getKey())));
                param.put(pair.getKey(), pair.getValue());
            }

            return values.toArray(new PropertyValue[values.size()]);
        }

        return null;
    }

    public static PropertyValue[] toPropertyValues(Map<String, Object> param, Map<String, Object> pairs) {
        if (pairs.size() > 0) {
            List<PropertyValue> values = new ArrayList<>();
            for (String key: pairs.keySet()) {
                values.add(value(key, param(key)));
                param.put(key, pairs.get(key));
            }

            return values.toArray(new PropertyValue[values.size()]);
        }

        return null;
    }
}
