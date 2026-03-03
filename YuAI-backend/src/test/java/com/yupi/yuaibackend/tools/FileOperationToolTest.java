package com.yupi.yuaibackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class FileOperationToolTest {

	@Test
	void readFile() {
		FileOperationTool fileOperationTool = new FileOperationTool();
		// 定义文件名称
		String fileName = "Github = Karry.txt";
		// 调用读文件方法
		String result = fileOperationTool.readFile(fileName);
		Assertions.assertNotNull(result);
	}

	@Test
	void writeFile() {
		FileOperationTool fileOperationTool = new FileOperationTool();
		// 定义要写的文件名称
		String fileName = "Github = Karry.txt";
		// 定义要写的文件内容
		String content = "我的Github为：https://github.com/Karry178";
		// 调用写文件的方法
		String result = fileOperationTool.writeFile(fileName, content);
		Assertions.assertNotNull(result);
	}
}