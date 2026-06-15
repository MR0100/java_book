package com.javamastery.examples.wallet.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Serializes a {@link WalletEvent} to the JSON stored in the {@code event_store.payload} column,
 * and reconstructs the typed event on the way back out.
 *
 * <p><strong>Why store the type separately and switch on it?</strong> The persisted row keeps an
 * {@code event_type} discriminator alongside the JSON. On read we route by that string to the
 * concrete record class. We deliberately do NOT use Jackson polymorphic type info embedded in the
 * JSON, because the discriminator is the schema-evolution seam: if we rename a Java class we only
 * remap the {@code type()} string, and old rows keep deserializing.
 *
 * <p>Jackson deserializes directly into Java records (it matches JSON fields to the canonical
 * constructor's components), so no setters or no-arg constructors are needed — events stay
 * immutable.
 */
@Component
public class EventSerializer {

    private final ObjectMapper mapper;

    /** Uses the application's pre-configured Jackson mapper (JavaTimeModule registered by Boot). */
    public EventSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /** Event -> JSON string for the payload column. */
    public String serialize(WalletEvent event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize event " + event.type(), e);
        }
    }

    /**
     * (eventType, payload JSON) -> typed event. The {@code switch} is exhaustive over the sealed
     * hierarchy's discriminators; an unknown type means a corrupt or future-version row and is a
     * hard error rather than a silently dropped event.
     */
    public WalletEvent deserialize(String eventType, String payload) {
        try {
            return switch (eventType) {
                case "MoneyDeposited" -> mapper.readValue(payload, MoneyDeposited.class);
                case "MoneyWithdrawn" -> mapper.readValue(payload, MoneyWithdrawn.class);
                case "MoneyTransferredOut" -> mapper.readValue(payload, MoneyTransferredOut.class);
                case "MoneyTransferredIn" -> mapper.readValue(payload, MoneyTransferredIn.class);
                default -> throw new IllegalStateException("Unknown event type: " + eventType);
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize event " + eventType, e);
        }
    }
}
