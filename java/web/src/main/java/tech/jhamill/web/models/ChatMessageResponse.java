package tech.jhamill.web.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class ChatMessageResponse {
    private ChatMessage data;
    private List<SourceContent> sources;
}
