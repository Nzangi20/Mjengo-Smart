package com.mjengo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PersistentStoreService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public PersistentStoreService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS app_store (
                  store_key VARCHAR(120) PRIMARY KEY,
                  payload CLOB
                )
                """);
    }

    public synchronized <T> List<T> loadList(String key, Class<T> clazz) {
        List<String> rows = jdbcTemplate.query(
                "SELECT payload FROM app_store WHERE store_key = ?",
                (rs, rowNum) -> rs.getString("payload"),
                key);
        if (rows.isEmpty() || rows.get(0) == null || rows.get(0).isBlank()) {
            return Collections.emptyList();
        }
        try {
            JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(rows.get(0), listType);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    public synchronized void saveList(String key, List<?> value) {
        try {
            String payload = objectMapper.writeValueAsString(value);
            int updated = jdbcTemplate.update(
                    "UPDATE app_store SET payload = ? WHERE store_key = ?",
                    payload, key);
            if (updated == 0) {
                jdbcTemplate.update(
                        "INSERT INTO app_store (store_key, payload) VALUES (?, ?)",
                        key, payload);
            }
        } catch (JsonProcessingException ignored) {
        }
    }
}
