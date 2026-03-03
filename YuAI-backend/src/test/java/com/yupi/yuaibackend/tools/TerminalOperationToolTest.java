package com.yupi.yuaibackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalOperationToolTest {

	@Test
	void executeTerminalCommand() {
		TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
		// 定义命令 - dir：显示当前目录所有文件
		String command = "ls -l";
		String result = terminalOperationTool.executeTerminalCommand(command);
		Assertions.assertNotNull(result);
	}
}