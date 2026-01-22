package com.yupi.yuaibackend.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 创建上下文查询增强器的工厂
 */
public class LoveAppContextualQueryAugmenterFactory {

	/**
	 * 自定义查不到相关 RAG 内容时，可以回复自定义空提示词，或者自动调用大模型回复
	 * @return
	 */
	public static ContextualQueryAugmenter createInstance() {

		// 自定义空提示词模版
		PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
				你应该输出下面的内容：
				抱歉，我只能回答恋爱相关的问题，别的办法没办法回复您，
				有问题可以联系人工客服：Karry
				""");

		// 返回上下文查询增强器
		return ContextualQueryAugmenter.builder()
				// 允许上下文为空：false
				.allowEmptyContext(false)
				// 把空提示词模版修改为上面定义的内容
				.emptyContextPromptTemplate(emptyContextPromptTemplate)
				.build();
	}
}
