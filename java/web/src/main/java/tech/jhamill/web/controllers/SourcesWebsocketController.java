package tech.jhamill.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import tech.jhamill.web.ai.RemoteHtmlEmbeddingStoreIngestor;
import tech.jhamill.web.models.CreateSourceInput;
import tech.jhamill.web.models.CreateSourceResponse;

import java.net.MalformedURLException;
import java.net.URL;

@Controller
public class SourcesWebsocketController {
    private final RemoteHtmlEmbeddingStoreIngestor embeddingStoreIngestor;

    @Autowired
    public SourcesWebsocketController(RemoteHtmlEmbeddingStoreIngestor embeddingStoreIngestor) {
        this.embeddingStoreIngestor = embeddingStoreIngestor;
    }

    @MessageMapping("/ingest")
    @SendTo("/topic/ingeststatus")
    public CreateSourceResponse ingest(CreateSourceInput body) {
        URL url;
        try {
            url = new URL(body.getUrl());
        } catch (MalformedURLException e) {
            return CreateSourceResponse.builder()
                    .message("Invalid URL format")
                    .successful(false)
                    .build();
        }

        embeddingStoreIngestor.ingest(url);
        return CreateSourceResponse.builder()
                .message("Successfully ingested a new source!")
                .successful(true)
                .build();
    }
}
