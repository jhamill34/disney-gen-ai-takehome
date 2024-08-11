package tech.jhamill.web.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateSourceInput {
    private String url;
}
