package tech.jhamill.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.jhamill.web.ai.RemoteHtmlEmbeddingStoreIngestor;
import tech.jhamill.web.models.CreateSourceInput;

import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping("/api/ingest")
public class SourcesController {
    private final RemoteHtmlEmbeddingStoreIngestor embeddingStoreIngestor;

    @Autowired
    public SourcesController(RemoteHtmlEmbeddingStoreIngestor embeddingStoreIngestor) {
        this.embeddingStoreIngestor = embeddingStoreIngestor;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void ingest(@RequestBody CreateSourceInput body) {
        URL url;
        try {
            url = new URL(body.getUrl());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad URL Format", e);
        }

        embeddingStoreIngestor.ingest(url);
    }
}
