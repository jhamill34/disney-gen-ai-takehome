package tech.jhamill.web.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Data
@Builder
@Jacksonized
public class SourceContent {
    private String text;
    private Map<String, Object> metadata;
}
