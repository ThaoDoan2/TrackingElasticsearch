package com.example.rest_service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CommaSeparatedStringOrArrayDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        List<String> values = new ArrayList<>();
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_STRING) {
            addCommaSeparated(values, parser.getValueAsString());
            return values;
        }

        if (token == JsonToken.START_ARRAY) {
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if (parser.currentToken() == JsonToken.VALUE_STRING) {
                    addCommaSeparated(values, parser.getValueAsString());
                } else {
                    String raw = parser.getValueAsString();
                    if (raw != null) {
                        addCommaSeparated(values, raw);
                    }
                }
            }
            return values;
        }

        String fallback = parser.getValueAsString();
        if (fallback != null) {
            addCommaSeparated(values, fallback);
        }
        return values;
    }

    private static void addCommaSeparated(List<String> values, String raw) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }
    }
}
