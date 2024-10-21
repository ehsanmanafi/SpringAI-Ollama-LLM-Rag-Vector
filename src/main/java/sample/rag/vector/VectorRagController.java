package sample.rag.vector;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VectorRagController {

    private final ChatClient chatClient;

    public VectorRagController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore,SearchRequest.defaults()))
                .build();
    }

    @GetMapping("/ask")
    public String faq(@RequestParam(value = "message", defaultValue = "Who is Ehsan Manafi Mourkani?") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

}
