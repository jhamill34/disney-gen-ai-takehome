package tech.jhamill.web.controllers;

import dev.langchain4j.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import tech.jhamill.web.ai.Assistant;
import tech.jhamill.web.models.ChatMessage;
import tech.jhamill.web.models.ChatMessageResponse;
import tech.jhamill.web.models.SourceContent;

import java.util.List;

@Controller
public class ChatWebsocketController {
    private final Assistant assistant;

    @Autowired
    public ChatWebsocketController(Assistant assistant) {
        this.assistant = assistant;
    }

    @MessageMapping("/post")
    @SendTo("/topic/responses")
    public ChatMessageResponse postMessage(ChatMessage chatMessage) {
        // TODO: This allows for OpenAI to take its time and respond when
        //   we're ready but if we have multiple clients, the initial message
        //   won't get broadcast to all other clients (we would need to
        //   handle deduplication for the initial client). For this we'll
        //   need an external message broker like RabbitMQ or ActiveMQ
        Result<String> response = assistant.chat(chatMessage.getSenderId(), chatMessage.getMessage());

        List<SourceContent> sources = response.sources()
                .stream()
                .map(s -> SourceContent.builder()
                        .text(s.textSegment().text())
                        .metadata(s.textSegment().metadata().toMap())
                        .build())
                .toList();

        return ChatMessageResponse.builder()
                .sources(sources)
                .data(ChatMessage.builder()
                        .senderId("DisneyAI")
                        .message(response.content())
                        .build())
                .build();
    }
}
