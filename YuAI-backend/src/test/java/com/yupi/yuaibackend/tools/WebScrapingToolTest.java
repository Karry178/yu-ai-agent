package com.yupi.yuaibackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WebScrapingToolTest {

	@Test
	void scrapeWebPage() {
		WebScrapingTool webScrapingTool = new WebScrapingTool();
		String url = "https://www.github.com/Karry178";
		String result = webScrapingTool.scrapeWebPage(url);
		Assertions.assertNotNull(result);
	}
}