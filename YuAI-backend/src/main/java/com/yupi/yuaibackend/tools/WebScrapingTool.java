package com.yupi.yuaibackend.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 网页抓取工具
 */
public class WebScrapingTool {

	/**
	 * 使用jsoup抓取网页
	 * @param url
	 * @return
	 */
	@Tool(description = "Scrape the content of a web page")
	public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
		// 直接使用jsoup抓取网页数据 - 要用try-catch包住
		try {
			Document document = Jsoup.connect(url).get();
			return document.html();
		} catch (Exception e) {
			return "Error occurred while scraping the web page: " + e.getMessage();
		}
	}
}
