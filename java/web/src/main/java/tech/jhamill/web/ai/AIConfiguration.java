package tech.jhamill.web.ai;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.injector.ContentInjector;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIConfiguration {
    @Bean
    @Primary
    public Tokenizer tokenizer(OpenAiChatModel languageModel) {
        return new OpenAiTokenizer(languageModel.modelName());
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel) {
        // TODO: we need these values in a property file...
        return PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .user("disney-th")
                .password("disney-th")
                .database("disney-th")
                .table("embedded_documents")
                .dimension(embeddingModel.dimension())
                .build();
    }

    @Bean
    public QueryTransformer queryTransformer(ChatLanguageModel chatLanguageModel) {
        return new CompressingQueryTransformer(chatLanguageModel);
    }

    @Bean
    public QueryRouter queryRouter(ContentRetriever contentRetriever) {
        // TODO: consider the LLM version...
        return new DefaultQueryRouter(contentRetriever);
    }

    @Bean
    public ContentAggregator contentAggregator() {
        return new DefaultContentAggregator();
    }

    @Bean
    public ContentInjector contentInjector() {
        // TODO: We can configure this..
        return new DefaultContentInjector();
    }

    @Bean
    public ChatMemoryProvider chatMemory() {
        return (id) -> MessageWindowChatMemory.withMaxMessages(5);
    }

    @Bean
    public RetrievalAugmentor retrievalAugmentor(
            QueryTransformer queryTransformer,
            QueryRouter queryRouter,
            ContentAggregator contentAggregator,
            ContentInjector contentInjector
    ) {
        return DefaultRetrievalAugmentor.builder()
                .queryTransformer(queryTransformer)
                .queryRouter(queryRouter)
                .contentAggregator(contentAggregator)
                .contentInjector(contentInjector)
                // .executor(...)
                .build();
    }
}
