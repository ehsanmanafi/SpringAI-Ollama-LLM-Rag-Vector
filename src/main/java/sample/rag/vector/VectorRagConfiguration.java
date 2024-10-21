package sample.rag.vector;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class VectorRagConfiguration {
    
    @Value("vectorStoreDatabase.json")
    private String vectorStoreDatabase;

    @Value("classpath:/document/end.pdf")
    private Resource pdfResource;

    @Bean
    SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) throws IOException {
        var simpleVectorStore = new SimpleVectorStore(embeddingModel);

        Path path = Paths.get("demo","src", "main", "resources", "data");
        String absolutePath = path.toFile().getAbsolutePath() + "/" + vectorStoreDatabase;
        var vectorStoreFile = new File(absolutePath);

        if (vectorStoreFile.exists()) {
            simpleVectorStore.load(vectorStoreFile);
        } else {
            var config= PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(0)
                            .withNumberOfTopPagesToSkipBeforeDelete(0)
                            .build())
                    .withPagesPerDocument(1)
                    .build();
            PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource,config);
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(pdfReader.get());
            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
        }
        return simpleVectorStore;
    }

}
