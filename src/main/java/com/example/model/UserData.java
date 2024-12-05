package com.example.model;

import java.util.Map;
import java.util.HashMap;

public class UserData {
    private String userId;
    private Map<String, String> attributes;

    public UserData() {
        this.attributes = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }
}
