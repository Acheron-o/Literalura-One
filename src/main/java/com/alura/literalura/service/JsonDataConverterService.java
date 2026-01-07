package com.alura.literalura.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.literalura.exception.BookVerseException;
import org.springframework.stereotype.Service;

/**
 * Service for converting JSON data to Java objects and vice versa.
 * Uses Jackson ObjectMapper for robust JSON processing with proper error handling.
 */
@Service
public class JsonDataConverterService {
    
    private final ObjectMapper objectMapper;

    public JsonDataConverterService() {
        this.objectMapper = new ObjectMapper();
        // Configure ObjectMapper for better JSON handling
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Converts JSON string to the specified class type.
     * @param <T> The type to convert to
     * @param json The JSON string to convert
     * @param clazz The target class
     * @return The converted object
     * @throws BookVerseException if JSON parsing fails
     */
    public <T> T convertData(String json, Class<T> clazz) throws BookVerseException {
        if (json == null || json.trim().isEmpty()) {
            throw new BookVerseException("JSON string cannot be null or empty");
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new BookVerseException("Failed to parse JSON: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BookVerseException("Unexpected error during JSON conversion: " + e.getMessage(), e);
        }
    }

    /**
     * Converts Java object to JSON string.
     * @param object The object to convert
     * @return The JSON string representation
     * @throws BookVerseException if JSON serialization fails
     */
    public String convertToJson(Object object) throws BookVerseException {
        if (object == null) {
            throw new BookVerseException("Object to convert cannot be null");
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BookVerseException("Failed to serialize object to JSON: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BookVerseException("Unexpected error during JSON serialization: " + e.getMessage(), e);
        }
    }

    /**
     * Converts Java object to pretty-formatted JSON string.
     * @param object The object to convert
     * @return The pretty-formatted JSON string
     * @throws BookVerseException if JSON serialization fails
     */
    public String convertToPrettyJson(Object object) throws BookVerseException {
        if (object == null) {
            throw new BookVerseException("Object to convert cannot be null");
        }

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BookVerseException("Failed to serialize object to pretty JSON: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new BookVerseException("Unexpected error during pretty JSON serialization: " + e.getMessage(), e);
        }
    }

    /**
     * Validates if a string is valid JSON.
     * @param json The string to validate
     * @return true if the string is valid JSON, false otherwise
     */
    public boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * Extracts a specific field from JSON string.
     * @param json The JSON string
     * @param fieldName The field name to extract
     * @return The field value as string, or null if not found
     * @throws BookVerseException if JSON parsing fails
     */
    public String extractField(String json, String fieldName) throws BookVerseException {
        if (json == null || json.trim().isEmpty()) {
            throw new BookVerseException("JSON string cannot be null or empty");
        }

        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new BookVerseException("Field name cannot be null or empty");
        }

        try {
            var jsonNode = objectMapper.readTree(json);
            var fieldNode = jsonNode.get(fieldName);
            return fieldNode != null ? fieldNode.asText() : null;
        } catch (JsonProcessingException e) {
            throw new BookVerseException("Failed to parse JSON for field extraction: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if JSON contains a specific field.
     * @param json The JSON string
     * @param fieldName The field name to check
     * @return true if the field exists, false otherwise
     * @throws BookVerseException if JSON parsing fails
     */
    public boolean hasField(String json, String fieldName) throws BookVerseException {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        if (fieldName == null || fieldName.trim().isEmpty()) {
            return false;
        }

        try {
            var jsonNode = objectMapper.readTree(json);
            return jsonNode.has(fieldName);
        } catch (JsonProcessingException e) {
            throw new BookVerseException("Failed to parse JSON for field check: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the ObjectMapper instance for advanced operations.
     * @return The configured ObjectMapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
