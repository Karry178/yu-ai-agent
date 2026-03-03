package com.yupi.yuaibackend.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 【样板代码】终端操作工具
 */
public class TerminalOperationTool {

    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
        // 1.构造输出对象，用于给最终结果
        StringBuilder output = new StringBuilder();
        try {
            // windows系统专属：
            // 2.使用ProcessBuilder执行命令的语法，得到一个进程对象
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "c", command);
            Process process = builder.start();

            // 2.使用Runtime：java内置的执行命令的语法，得到一个进程对象
            // Process process = Runtime.getRuntime().exec(command);
            // 3.由进程对象取出进程输入流，得到进程输出结果
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            // 4.等待命令执行完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}
