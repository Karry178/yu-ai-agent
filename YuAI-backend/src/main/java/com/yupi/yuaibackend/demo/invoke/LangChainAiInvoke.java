package com.yupi.yuaibackend.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class LangChainAiInvoke {

    public static void main(String[] args) {
        // 构造chatModel
        QwenChatModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-max")
                .build();
        // 调用chatModel
        String answer = qwenChatModel.chat("我是程序员Karry，我的Github地址为：http://github.com/Karry178");
        System.out.println(answer);
    }
}
