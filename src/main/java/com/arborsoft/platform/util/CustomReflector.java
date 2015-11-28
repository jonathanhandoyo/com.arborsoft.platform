package com.arborsoft.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomReflector {
    private static final Logger LOG = LoggerFactory.getLogger(CustomReflector.class);

    public static List<Field> getFields(Class<?> _class) {
        Class<?> current = _class;
        List<Field> all = new ArrayList<>();
        while (current != null) {
            for (Field field : current.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
                    all.add(field);
                }
            }
            current = current.getSuperclass();
        }
        return all;
    }

    public static Map<String, Object> introspect(Object obj) {
        try {
            if (obj != null) {
                Map<String, Object> result = new HashMap<>();
                for (Field field : getFields(obj.getClass())) {
                    if ("node".equals(field.getName())) continue;
                    if ("relationship".equals(field.getName())) continue;

                    field.setAccessible(true);
                    if (field.get(obj) != null) {
                        if (field.getClass().equals(Class.class)) result.put(field.getName(), ((Class<?>) field.get(obj)).getSimpleName());
                        else result.put(field.getName(), field.get(obj));
                    }
                }
                return result;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
