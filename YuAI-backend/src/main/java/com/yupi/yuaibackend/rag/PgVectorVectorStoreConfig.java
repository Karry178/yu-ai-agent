package com.yupi.yuaibackend.rag;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
@Slf4j
public class PgVectorVectorStoreConfig {

	// 引入 文档加载器
	@Resource
	private LoveAppDocumentLoader loveAppDocumentLoader;

	/**
	 * 创建 PostgreSQL 数据源（用于向量存储）
	 */
	@Bean(name = "pgVectorDataSource")
	@Lazy
	@ConfigurationProperties(prefix = "pgvector.datasource")
	public DataSource pgVectorDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	/**
	 * 创建 PostgreSQL 的 JdbcTemplate
	 */
	@Bean(name = "pgVectorJdbcTemplate")
	public JdbcTemplate pgVectorJdbcTemplate(@Qualifier("pgVectorDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	/**
	 * 创建 PgVector 向量存储
	 */
	@Bean
	public VectorStore pgVectorVectorStore(
			@Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate,
			EmbeddingModel dashscopeEmbeddingModel) {
		
		PgVectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
				.dimensions(1536)
				// 编辑距离
				.distanceType(COSINE_DISTANCE)
				// 索引
				.indexType(HNSW)
				// 自动初始化建表
				.initializeSchema(true)
				.schemaName("public")
				// 向量表名
				.vectorTableName("vector_store")
				// 单词最大插入量
				.maxDocumentBatchSize(10000)
				.build();

		// 加载文档
		List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
		vectorStore.add(documents);
		return vectorStore;
	}
}
