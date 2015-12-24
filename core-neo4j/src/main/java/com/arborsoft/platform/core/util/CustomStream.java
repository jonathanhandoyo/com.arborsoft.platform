package com.arborsoft.platform.core.util;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CustomStream {
    public static <I extends Iterator<T>, T> Stream<T> stream(I iterator) {
        return stream(iterator, false);
    }

    public static <I extends Iterator<T>, T> Stream<T> stream(I iterator, boolean parallel) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    public static <I extends Iterable<T>, T> Stream<T> stream(I iterable) {
        return stream(iterable, false);
    }

    public static <I extends Iterable<T>, T> Stream<T> stream(I iterable, boolean parallel) {
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }
}
