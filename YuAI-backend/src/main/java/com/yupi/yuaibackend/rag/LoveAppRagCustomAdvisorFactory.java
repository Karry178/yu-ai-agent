package com.yupi.yuaibackend.rag;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 创建自定义的 RAG 检索增强顾问的 工厂
 */
public class LoveAppRagCustomAdvisorFactory {

	/**
	 * 创建自定义的 RAG 检索增强顾问
	 * @param vectorStore
	 * @param status
	 * @return
	 */
	public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {

		// 3.最后定义一个过滤表达式：根据用户的婚恋状态过滤文档
		Filter.Expression expression = new FilterExpressionBuilder()
				.eq("status", status)
				.build();

		// 2.再定义一个文档检索器
		VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
				// 创建Advisor时，传入一个VectorStore，在外层创建
				.vectorStore(vectorStore)
				// 加入过滤表达式 -> 3.定义一个过滤表达式：根据用户的婚恋状态过滤文档
				.filterExpression(expression)
				// 设置相似度阈值
				.similarityThreshold(0.5)
				.topK(3)
				.build();

		// 1.先建一个 检索增强顾问
		return RetrievalAugmentationAdvisor.builder()
				// 文档检索器 - 自定义文档过滤条件 -> 2.定义一个文档检索器
				.documentRetriever(documentRetriever)
				// 文档增强器 -> 自定义了空提示词，如果查询为空值且默认允许为空，则自动调用大模型回复问题
				.queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
				.build();
	}
}
