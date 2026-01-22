package com.yupi.yuaibackend.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest(properties =
		"spring.autoconfigure.exclude=org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration,org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration")
class PgVectorVectorStoreConfigTest {

	// 引入 VectorStore
	@Resource
	private VectorStore pgVectorVectorStore;


	@Test
	void pgVectorVectorStore() {

		List<Document> documents = List.of(
				new Document("学好编程有什么用？", Map.of("meta1", "meta1")),
				new Document("做一个程序员最重要的是什么？"),
				new Document("你觉得明星里面谁比较帅气？", Map.of("meta2", "meta2")));

		// 添加文档
		pgVectorVectorStore.add(documents);
		// 相似度查询
		List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("怎么学变成啊？").topK(3).build());
		Assertions.assertNotNull(results);
	}
}