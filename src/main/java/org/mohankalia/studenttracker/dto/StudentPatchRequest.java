package org.mohankalia.studenttracker.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class StudentPatchRequest {

    private final Map<String, Object> fields = new LinkedHashMap<>();

    @JsonAnySetter
    public void addField(String name, Object value) {
        fields.put(name, value);
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}
