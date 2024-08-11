package tech.jhamill.web.ai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface Assistant {
    @SystemMessage("You are an expert journalist who answers questions about articles.")
    Result<String> chat(@MemoryId String sender, @UserMessage String userMessage);
}
