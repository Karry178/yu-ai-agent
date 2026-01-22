package com.yupi.yuaibackend.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 恋爱大师应用 文档加载器
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {

    // Spring内置的资源解析类
    private final ResourcePatternResolver resourcePatternResolver;

    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }


    /**
     * 加载多篇markdown文档
     * @return
     */
    public List<Document> loadMarkdowns() {
        // 新建Document的List
        List<Document> allDocuments = new ArrayList<>();
        // 加载多篇markdown文档
        try {
            // 根据目标md文件的路径得到Resource目录
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            // 加载单篇md文件到内存中，转化为List<Document>的文档对象
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                // 对文件名进行切分，得到状态：XX篇 （XX为状态）
                String status = filename.substring(filename.length() - 6, filename.length() - 4);
                // 复用官方文档示例：定义一个markdown加载器
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        // 元信息：文件名
                        .withAdditionalMetadata("filename", filename)
                        // 元信息：状态
                        .withAdditionalMetadata("status", status)
                        .build();

                // 新建一个资源获取器，把资源与配置加进去
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            log.error("加载md文档失败", e);
        }
        return allDocuments;
    }
}
