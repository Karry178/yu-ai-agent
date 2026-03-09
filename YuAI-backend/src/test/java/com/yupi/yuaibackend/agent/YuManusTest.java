package com.yupi.yuaibackend.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YuManusTest {

	@Resource
	private YuManus yuManus;

	@Test
	public void run() {
		String userPrompt = """
				我想去衣冠庙地铁口附近的美食街，请帮我找到5KM以内的美食街，并结合一些网络图片，制定一份详细的逛街计划。
				并且以PDF格式输出。
				""";
		String answer = yuManus.run(userPrompt);
		Assertions.assertNotNull(answer);
	}
}