package tech.jhamill.web.ai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.HtmlTextExtractor;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class RemoteHtmlEmbeddingStoreIngestor {
    private final EmbeddingStoreIngestor ingestor;
    private final DocumentParser documentParser;

    @Autowired
    public RemoteHtmlEmbeddingStoreIngestor(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel,
            Tokenizer tokenizer) {
        this.documentParser = new TextDocumentParser();

        DocumentSplitter documentSplitter = DocumentSplitters.recursive(
                300, 0, tokenizer);
        DocumentTransformer docTransformer = new HtmlTextExtractor(
                "body", null, true);

        this.ingestor = EmbeddingStoreIngestor.builder()
                .documentTransformer(docTransformer)
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

    public void ingest(URL url) {
        Document document = UrlDocumentLoader.load(url, this.documentParser);
        this.ingestor.ingest(document);
    }
}
