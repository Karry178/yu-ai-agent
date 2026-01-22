package com.yupi.yuaibackend.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 AI 的文档元信息增强器（为文档补充元信息）
 */
@Component
public class MyKeywordEnricher {

	// 引入ChatModel
	@Resource
	private ChatModel dashscopeChatModel;

	public List<Document> enrichDocuments(List<Document> documents) {
		// 新建一个关键词元信息增强器
		KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel, 5);
		// 调用信息增强器的apply方法对 documents 访问
		return keywordMetadataEnricher.apply(documents);
	}
}
