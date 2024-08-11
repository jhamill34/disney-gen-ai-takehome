package tech.jhamill.web.controllers;

import dev.langchain4j.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhamill.web.ai.Assistant;
import tech.jhamill.web.models.ChatMessage;
import tech.jhamill.web.models.ChatMessageResponse;
import tech.jhamill.web.models.SourceContent;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final Assistant assistant;

    @Autowired
    public ChatController(Assistant assistant) {
        this.assistant = assistant;
    }

    @PostMapping
    public ChatMessageResponse postMessage(@RequestBody ChatMessage chatMessage) {
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
