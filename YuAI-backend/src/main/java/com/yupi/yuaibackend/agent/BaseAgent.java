package com.yupi.yuaibackend.agent;

import cn.hutool.core.util.StrUtil;
import com.yupi.yuaibackend.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理的状态和执行流程
 *
 * 提供状态转换，内存管理和基于步骤的执行循环的基础功能
 * 子类必须实现step方法
 */
@Data
@Slf4j
public abstract class BaseAgent {

	// 核心属性
	private String name;

	// 提示词（当前步 与 下一步）
	private String systemPrompt;
	private String nextStepPrompt;

	// 代理状态 - 默认是空闲状态
	private AgentState state = AgentState.IDLE;

	// 执行步骤控制
	private int currentStep = 0;
	private int maxSteps = 10;

	// LLM 大模型
	private ChatClient chatClient;

	// 会话上下文记忆
	private List<Message> messageList = new ArrayList<>();


	/**
	 * 运行代理/Agent
	 * @param userPrompt 用户提示词
	 * @return 执行结果
	 */
	public String run(String userPrompt) {
		// 1.基础校验
		if (this.state != AgentState.IDLE) {
			throw new RuntimeException("Cannot run agent from state: " + this.state);
		}
		if (StrUtil.isBlank(userPrompt)) {
			throw new RuntimeException("Cannot run agent with empty user prompt");
		}

		// 2.执行，更改状态
		this.state = AgentState.RUNNING;
		// 记录消息上下文
		messageList.add(new UserMessage(userPrompt));
		// 保存消息列表
		ArrayList<String> results = new ArrayList<>();

		// 3.执行循环 - try-catch-finally包围
		try {
			for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
				int stepNumber = i + 1;
				currentStep = stepNumber;
				// 日志：当前执行的步数
				log.info("Excuting step {}/{}", stepNumber, maxSteps);

				// 单步执行
				String stepResult = step();
				String result = "Step " + stepNumber + ":" + stepResult;
				results.add(result);
			}
		} catch (Exception e) {
			state = AgentState.ERROR;
			log.error("error executing agent", e);
			return "执行错误" + e.getMessage();
		} finally {
			// 清理资源
			this.cleanup();
		}

		// 4. 检查是否超出步骤限制
		if (currentStep >= maxSteps) {
			state = AgentState.FINISHED;
			results.add("Terminated: Reached max steps (" + maxSteps + ")");
		}
		return String.join("\n", results);
	}


	/**
	 * 运行代理（SSE流式输出运行超级智能体方法）
	 * @param userPrompt 用户提示词
	 * @return 执行结果
	 */
	public SseEmitter runStream(String userPrompt) {

		// 先创建一个超时时间较长的SseEmitter
		SseEmitter sseEmitter = new SseEmitter(300000L); // 5min超时

		// 然后使用异步处理把主程序括起来 ——> CompletableFuture.runAsync(() -> {})
		  // 如果不加异步处理，还是会逐步进行完毕才拿到结果，还是同步处理了
		CompletableFuture.runAsync(() -> {
			// 1.基础校验
			try {
				if (this.state != AgentState.IDLE) {
					sseEmitter.send("错误：无法从状态运行处理" + this.state);
					sseEmitter.complete();
					return;
				}
				if (StrUtil.isBlank(userPrompt)) {
					sseEmitter.send("错误：不能使用空提示词运行代理");
					sseEmitter.complete();;
					return;
				}
			} catch (IOException e) {
				sseEmitter.completeWithError(e);
			}

			// 2.执行，更改状态
			this.state = AgentState.RUNNING;
			// 记录消息上下文
			messageList.add(new UserMessage(userPrompt));
			// 保存消息列表
			ArrayList<String> results = new ArrayList<>();

			// 3.执行循环 - try-catch-finally包围
			try {
				for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
					int stepNumber = i + 1;
					currentStep = stepNumber;
					// 日志：当前执行的步数
					log.info("Excuting step {}/{}", stepNumber, maxSteps);

					// 单步执行
					String stepResult = step();
					String result = "Step " + stepNumber + ":" + stepResult;
					results.add(result);

					// 输出当前每一步的结果给SSE
					sseEmitter.send(result);
				}

				// 4. 检查是否超出步骤限制
				if (currentStep >= maxSteps) {
					state = AgentState.FINISHED;
					results.add("Terminated: Reached max steps (" + maxSteps + ")");
					sseEmitter.send("执行结束，达到最大步骤(" + maxSteps + ")");
				}
				// 正常完成
				sseEmitter.complete();
			} catch (Exception e) {
				state = AgentState.ERROR;
				log.error("error executing agent", e);
				try {
					sseEmitter.send("执行错误：" + e.getMessage());
					sseEmitter.complete();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			} finally {
				// 清理资源
				this.cleanup();
			}
		});

		// 设置超时时间
		sseEmitter.onTimeout(() -> {
			// 超时则设置Agent的状态为Error，并清理资源
			this.state = AgentState.ERROR;
			this.cleanup();
			log.warn("SSE connection timeout");
		});
		// 若正常完成，则改Agent状态为Finished 并 清理资源
		sseEmitter.onCompletion(() -> {
			if (this.state == AgentState.RUNNING) {
				this.state = AgentState.FINISHED;
			}
			this.cleanup();
		});

		return sseEmitter;
	}


	/**
	 * 定义单个步骤
	 *
	 * 每一个操作执行的步骤要交给子类实现，所以定义一个抽象方法
	 * @return
	 */
	public abstract String step();


	/**
	 * 清理资源
	 */
	public void cleanup() {
		// 子类可以重写此方法清理资源
	}
}
