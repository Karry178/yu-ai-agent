package com.yupi.yuaibackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceDownloadToolTest {

	@Test
	void downloadResource() {
		ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
		String url = "https://www.github.com/Karry178";
		String fileName = "karry.html";
		String result = resourceDownloadTool.downloadResource(url, fileName);
		Assertions.assertNotNull(result);
	}
}