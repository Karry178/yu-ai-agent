package com.yupi.yuaibackend.demo.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MultiQueryExpanderDemo {

	private final ChatClient.Builder chatClientBuilder;

	// 使用构造器注入 ChatModel（Spring 会自动注入）
	public MultiQueryExpanderDemo(ChatModel dashscopeChatModel) {
		this.chatClientBuilder = ChatClient.builder(dashscopeChatModel);
	}

	public List<Query> expand(String query) {
		MultiQueryExpander queryExpander = MultiQueryExpander.builder()
				.chatClientBuilder(chatClientBuilder)
				.numberOfQueries(3)
				.build();
		List<Query> queries = queryExpander.expand(new Query("谁是程序员Karry啊？"));
		return queries;
	}
}
