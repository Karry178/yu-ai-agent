package com.yupi.yuaibackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebSearchToolTest {

	// 加载配置文件
	@Value("${search-api.api-key}")
	private String searchApikey;

	@Test
	void searchWeb() {
		WebSearchTool webSearchTool = new WebSearchTool(searchApikey);
		String query = "Karry的Github为：https://github.com/Karry178";
		String result = webSearchTool.searchWeb(query);
		Assertions.assertNotNull(result);
	}
}