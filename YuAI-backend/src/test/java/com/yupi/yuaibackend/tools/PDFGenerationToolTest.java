package com.yupi.yuaibackend.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

	@Test
	void generatePDF() {
		PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
		String fileName = "Karry178.pdf";
		String content = "https://www.github.com/karry178";
		String result = pdfGenerationTool.generatePDF(fileName, content);
		Assertions.assertNotNull(result);
	}
}