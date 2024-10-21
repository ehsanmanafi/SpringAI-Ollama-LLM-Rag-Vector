package sample.rag.vector;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class VectorRagWithPromptTemplateController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;


    @Value("classpath:/prompts/promptBody.st")
    private Resource samplePromptTemplate;

    public VectorRagWithPromptTemplateController(ChatClient.Builder chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/ask2")
    public String faq(@RequestParam(value = "message", defaultValue = "Who is Ehsan Manafi Mourkani?") String message) {

        PromptTemplate promptTemplate=new PromptTemplate(samplePromptTemplate);

        List<Document> searchResultDocuments = vectorStore.similaritySearch(SearchRequest.query(message).withTopK(3));
        return chatClient.prompt(promptTemplate.create(Map.of("input",message,"documents",searchResultDocuments)))
                .call()
                .content();
    }
}
