package com.yupi.yuaibackend.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义基于阿里云知识库服务的 RAG 增强顾问
 */
@Configuration
@Slf4j
public class LoveAppRagCloudAdvisorConfig {

	// 调用阿里云大模型
	@Value("${spring.ai.dashscope.api-key}")
	private String dashscopeApikey;

	@Bean
	public Advisor loveAppRagCloudAdvisor() {
		// 先拿到Apikey
		DashScopeApi dashScopeApi = new DashScopeApi(dashscopeApikey);
		// 定义阿里云知识库索引名，即知识库名字
		final String KNOWLEDGE_INDEX = "恋爱大师秘籍";

		// 构造DocumentRetriever，即文档检索器
		DashScopeDocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi, DashScopeDocumentRetrieverOptions.builder()
				// 指定知识库索引名字
				.withIndexName(KNOWLEDGE_INDEX)
				.build());

		// 再用文档检索器DocumentRetriever构造 RAG
		return RetrievalAugmentationAdvisor.builder()
				// 指定文档检索器
				.documentRetriever(dashScopeDocumentRetriever)
				.build();
	}
}
