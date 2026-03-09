package com.yupi.yuaibackend.controller;

import com.yupi.yuaibackend.agent.YuManus;
import com.yupi.yuaibackend.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

	// 注入LoveApp
	@Resource
	private LoveApp loveApp;

	// 注入YuManus使用的工具 - ToolCallback[]
	@Resource
	private ToolCallback[] allTools;

	// 注入大模型
	@Resource
	private ChatModel dashscopeChatModel;


	/**
	 * 同步调用AI恋爱大师应用
	 *
	 * @param message
	 * @param chatId
	 * @return
	 */
	@GetMapping("/love_app/chat/sync")
	public String doChatWithLoveAppSync(String message, String chatId) {
		return loveApp.doChat(message, chatId);
	}


	/**
	 * SSE 流式调用 AI 恋爱大师应用
	 *
	 * 第1种方式：produces = MediaType.TEXT_EVENT_STREAM_VALUE，通过这个，http的响应头中就可以看到前端以流式传输
	 * @param message
	 * @param chatId
	 * @return
	 */
	@GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
		return loveApp.doChatByStream(message, chatId);
	}


	/**
	 * SSE 流式调用 AI 恋爱大师应用
	 *
	 * 第2种方式：使用ServerSentEvent泛型包装String，通过这个，http的响应头中就可以看到前端以流式传输
	 * @param message
	 * @param chatId
	 * @return
	 */
	@GetMapping("/love_app/chat/server_sent_event")
	public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
		// 把响应式对象转为ServerSentEvent对象
		return loveApp.doChatByStream(message, chatId)
				// 把每次生成的碎片化信息chunk 构造为 ServerSentEvent，通过<String>Builder方法构造
				.map(chunk -> ServerSentEvent.<String>builder()
						// 然后把chunk文本片段作为流式响应的data数据
						.data(chunk)
						.build());
	}


	/**
	 * SSE 流式调用 AI 恋爱大师应用
	 *
	 * 【更推荐】第3种方式：produces = MediaType.TEXT_EVENT_STREAM_VALUE，通过这个，http的响应头中就可以看到前端以流式传输
	 * @param message
	 * @param chatId
	 * @return
	 */
	@GetMapping("/love_app/chat/sse_emitter")
	public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
		// 1.先定义一个 SseEmitter，可以定义超时时间 —— 创建一个超时时间较长的SseEmitter
		SseEmitter sseEmitter = new SseEmitter(180000L);

		// 2.监听响应对象
		loveApp.doChatByStream(message, chatId)
				// 获取Flux响应式数据流 并直接通过订阅推送给SSEEmitter
				// 通过订阅响应对象方法 即可 监听，把每个消息快chunk直接推过去
				.subscribe(chunk -> {
					try {
						sseEmitter.send(chunk);
					} catch (IOException e) {
						sseEmitter.completeWithError(e);
					}
					// 传递第二个参数 e -> {sseEmitter.completeWithError(e)}，可简化为 sseEmitter::completeWithError
					// 传递第三个参数，目的是对sseEmitter完成  sseEmitter::complete)
				}, sseEmitter::completeWithError, sseEmitter::complete);
		return sseEmitter;
	}


	/**
	 * 流式调用 -> 与自主规划的超级智能体 对话
	 *
	 * @param message
	 * @return
	 */
	@GetMapping("/manus/chat")
	public SseEmitter doChatWithManus(String message) {
		// 新建YuManus
		YuManus yuManus = new YuManus(allTools, dashscopeChatModel);
		return yuManus.runStream(message);
	}
}
