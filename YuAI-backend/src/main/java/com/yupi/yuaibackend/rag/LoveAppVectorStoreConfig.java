package com.yupi.yuaibackend.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentMetadata;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 恋爱大师APP 向量数据库配置（初始化基于内存的向量数据库 Bean）
 */
@Configuration
public class LoveAppVectorStoreConfig {

	// 引入文档加载器
	@Resource
	private LoveAppDocumentLoader loveAppDocumentLoader;

	// 引入Token切词器
	@Resource
	private MyTokenTextSplitter myTokenTextSplitter;

	// 引入关键词元信息增强器
	@Resource
	private MyKeywordEnricher myKeywordEnricher;


	@Bean
	VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
		// 初始化一个基于内存的VectorStore存储
		SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
		// 然后拿到全部的Document
		List<Document> documentList = loveAppDocumentLoader.loadMarkdowns();

		// 1.把得到的文档 自主切分
		// List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documentList);

		// 2.使用关键词元信息增强器 -> 自动补充关键词元信息
		List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documentList);

		// 把结果写入simpleVectorStore
		simpleVectorStore.add(enrichDocuments);
		return simpleVectorStore;
	}

}
