package tech.jhamill.web.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ChatMessage {
    private String message;
    private String senderId;
}
