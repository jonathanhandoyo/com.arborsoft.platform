package com.arborsoft.platform.core.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomCollection {
    public static <K, V> Pair[] unwind(Map<K, V> map) {
        if (map != null && !map.isEmpty()) {
            List<Pair<K, V>> pairs = new ArrayList<>();
            for (K key: map.keySet()) {
                pairs.add(Pair.of(key, map.get(key)));
            }

            return pairs.toArray(new Pair[pairs.size()]);
        }
        return null;
    }
}
