package com.yupi.yuaibackend.agent;

import com.yupi.yuaibackend.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

// 把YuManus定义为Bean（因为YuManus需要调用ChatClient，但CC需要springBoot启动才可以调用，故定义为Bean交给Spring管理）
@Component
public class YuManus extends ToolCallAgent {

	/**
	 * Karry的超级智能体（拥有自主规划能力，可直接使用）
	 * @param allTools
	 * @param dashscopeChatModel
	 */
	public YuManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
		super(allTools);
		this.setName("yuManus");
		// 定义prompt提示词
		String SYSTEM_PROMPT = """
				You are YuManus, an all-capable AI assistant, aimed at solving any task presented by the user,
				You have various tools at your disposal that you can call upon to efficiently complex requests.
				""";
		this.setSystemPrompt(SYSTEM_PROMPT);
		String NEXT_SYSTEM_PROMPT = """
				Based on user needs, proactively select the most appropriate tool or combination of tools.
				For complex tasks, you can break down the problem and use different tools step by step to solve it.
				After using each tool, clearly explain the execution results and suggest the next steps.
				If you want to stop the interaction at any point, use the  terminate tool/function call.
				""";
		this.setNextStepPrompt(NEXT_SYSTEM_PROMPT);
		// 设置最大步骤
		this.setMaxSteps(10);

		// 初始化 AI 对话客户端
		ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
				// 传入日志拦截器
				.defaultAdvisors(new MyLoggerAdvisor())
				.build();

		// 传递ChatClient客户端
		setChatClient(chatClient);
	}
}
