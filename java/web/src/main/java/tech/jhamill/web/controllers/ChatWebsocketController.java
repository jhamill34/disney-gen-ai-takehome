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
