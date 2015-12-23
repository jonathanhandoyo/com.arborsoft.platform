package com.arborsoft.platform.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.TreeMap;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
public class BaseDomain extends TreeMap<String, Object> {
    public Long getId() {
        return (Long) super.get("__id__");
    }

    public void setId(Long id) {
        Assert.notNull(id, "ID is null");

        super.put("__id__", id);
    }

    public Object get(String key) {
        Assert.notNull(key, "Key is null");

        return super.get(key);
    }

    public void set(String key, Object value) {
        Assert.notNull(key, "Key is null");
        Assert.notNull(value, "Value is null");
        Assert.state(key.startsWith("__") == false, "Reserved keyword: " + key);

        super.put(key, value);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseDomain _o = (BaseDomain) o;
        if (this.getId() == null) return super.equals(o);
        return this.getId().equals(_o.getId());
    }

    @Override
    public int hashCode() {
        return this.getId() != null ? this.getId().hashCode() : super.hashCode();
    }
}