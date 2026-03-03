package com.yupi.yuaibackend.tools;

import cn.hutool.core.io.FileUtil;
import com.yupi.yuaibackend.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类（提供文件读写功能）
 */
public class FileOperationTool {

	// 定义文件存储的常量
	private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

	/**
	 * 读取文件操作
	 * @Tool 工具介绍
	 * @ToolParm 工具的参数描述
	 * @param fileName
	 * @return
	 */
	@Tool(description = "Read content from a file")
	public String readFile(@ToolParam(description = "Name of a file to read") String fileName) {
		// 1.根据文件名称读取文件，获得路径
		String filePath = FILE_DIR + "/" + fileName;
		// 假设可能会出问题，做好异常处理
		try {
			// 2.使用Hutool工具类，根据路径读取文件
			return FileUtil.readUtf8String(filePath);
		} catch (Exception e) {
			return "Error reading file: " + e.getMessage();
		}
	}


	/**
	 * 写入文件操作
	 * @param fileName
	 * @param content
	 * @return
	 */
	@Tool(description = "Write content to a file")
	public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
	                        @ToolParam(description = "Content to write to the file") String content) {
		// 1.根据文件名读取文件，获得路径
		String filePath = FILE_DIR + "/" + fileName;
		// 2.创建文件目录
		FileUtil.mkdir(FILE_DIR);
		// try - catch 操作，保证代码健壮性
		try {
			// 3.写入文件内容
			FileUtil.writeUtf8String(content, filePath);
			return "File written successfully to: " + filePath;
		} catch (Exception e) {
			return "Error written to file: " + e.getMessage();
		}
	}
}
