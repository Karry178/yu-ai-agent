package com.yupi.yuaibackend.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@ActiveProfiles("local")
@SpringBootTest(properties =
        "spring.autoconfigure.exclude=org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration,org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration"
)
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    /**
     * 调用大模型进行对话
     */
    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员Karry";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想让另一半（编程导航）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }


    /**
     * 调用大模型 + prompt提示词 输出报告
     */
    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是程序员Karry，我想让另一半和我能走长久，但是我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }


    /**
     * 调用大模型，根据RAG文档内容进行对话
     */
    @Test
	void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我已经结婚了，但是婚后关系不太亲密，我不知道该怎么做";
        // 调用doChatWithRag()方法
        String loveReport = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(loveReport);
    }


    // 调用AI的工具能力
	@Test
	void doChatWithTools() {
        // 测试联网搜索问题的答案
        // testMessage("周末想带对象去洛阳约会，推荐几个适合情侣的热门打卡地点？");

        // 测试网页抓取：恋爱案例分析
        // testMessage("怎么保证恋爱的甜蜜度？请参考百度浏览器(www.baidu.com)的一些意见给出建议。");

        // 测试资源下载：图片下载功能
        testMessage("直接下载一张适合做手机壁纸的星空图片为文件");

        // 测试文件操作：保存用户档案
        // testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        // testMessage("生成一份春节去洛阳约会游玩的计划，要求是PDF格式的文件，包括订酒店、景点游玩规划");
	}

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图MCP
        /*String message = "我的另一半在成都武侯区，请帮我找到5KM内合适的约会地点";
        String answer = loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);*/

        // 测试图片搜素MCP
        String message = "帮我搜索约会有关的图片";
        String answer = loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
