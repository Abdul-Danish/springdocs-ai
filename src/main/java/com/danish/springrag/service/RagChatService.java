package com.danish.springrag.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RagChatService {

	private final JdbcClient jdbcClient;
	private final VectorStore vectorStore;

	@Value("classpath:/reference-docs/spring-boot-reference-doc.pdf")
	private Resource springRefDoc;

	public RagChatService(JdbcClient jdbcClient, VectorStore vectorStore) {
		this.jdbcClient = jdbcClient;
		this.vectorStore = vectorStore;
	}

	@PostConstruct
	public void init() {
		Integer count = jdbcClient.sql("SELECT COUNT(*) from vector.spring_docs_vector_store").query(Integer.class)
				.single();
		log.info("Vector Store Database Count: {}", count);

		if (count == 0) {
			log.info("Loading Vector Documents into the Database");

			PdfDocumentReaderConfig readerConfig = PdfDocumentReaderConfig.builder()
					.withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
							.withNumberOfBottomTextLinesToDelete(0).withNumberOfTopPagesToSkipBeforeDelete(0).build())
					.withPagesPerDocument(1).build();

			PagePdfDocumentReader pageReader = new PagePdfDocumentReader(springRefDoc, readerConfig);
			TokenTextSplitter textSplitter = TokenTextSplitter.builder().build();

			List<Document> chunks = textSplitter.apply(pageReader.get());
			log.info("Text splitting completed. Chunks count: {}", chunks.size());

			long start = System.currentTimeMillis();
			int batchSize = 100;
			for (int i = 0; i < chunks.size(); i += batchSize) {
				int end = Math.min(i + batchSize, chunks.size());
				vectorStore.accept(chunks.subList(i, end));

				log.info("Processed {} / {}", end, chunks.size());
			}
			logResponse(start);
			log.info("Vector Documents Loaded");
		}
	}

	private void logResponse(long start) {
		long durationMs = System.currentTimeMillis() - start;
		long minutes = durationMs / 60000;
		long seconds = (durationMs % 60000) / 1000;
		log.info("Vector store insert completed in {} min {} sec", minutes, seconds);
	}

}
