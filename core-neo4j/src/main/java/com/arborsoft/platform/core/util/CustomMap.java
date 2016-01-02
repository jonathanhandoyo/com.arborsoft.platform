package com.arborsoft.platform.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomMap {

    @Getter
    @Setter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Tuple<K, V> implements Map.Entry<K, V>, Serializable {
        private K key;
        private V value;

        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
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
    }

    @SafeVarargs
    public static <E> List<E> list(E... elements) {
        return Arrays.asList(elements);
    }

    public static <K, V> Map<K, V> map(Tuple<K, V>... tuples) {
        Collections.unmodifiableMap(Stream.of(tuples).collect(Collectors.toMap(Tuple::getKey, Tuple::getValue)));
        return null;
    }

    public static <K, V> Tuple<K, V> tuple(K key, V value) {
        return new Tuple(key, value);
    }

    public static <K, V> Tuple<K, V> entry(K key, V value) {
        return tuple(key, value);
    }
}
