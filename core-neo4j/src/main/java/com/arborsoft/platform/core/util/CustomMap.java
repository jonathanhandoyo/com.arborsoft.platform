package com.arborsoft.platform.core.util;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomMap {
    @SafeVarargs
    public static <K, V> Map<K, V> map(Map.Entry<K, V>... entries) {
        return Collections.unmodifiableMap(Stream.of(entries).collect(entriesToMap()));
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> entriesToMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }
}
