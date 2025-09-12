package com.example.quizzapp.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

/**
 * A standard wrapper for all events published asynchronously.
 * This ensures a consistent message structure and simplifies deserialization.
 *

 */
@Getter
public class EventEnvelope {
    private final String eventType;
    private final JsonNode payload; // Dùng JsonNode để linh hoạt

    @JsonCreator
    public EventEnvelope(@JsonProperty("eventType") String eventType,
                         @JsonProperty("payload") JsonNode payload) {
        this.eventType = eventType;
        this.payload = payload;
    }
}