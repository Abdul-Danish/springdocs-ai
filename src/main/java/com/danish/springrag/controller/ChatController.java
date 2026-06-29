package com.danish.springrag.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@Slf4j
public class ChatController {

	private final VectorStore vectorStore;
	private final ChatClient chatClient;

	@Value("classpath:/prompt-templates/spring_assistant_prompt.st")
	private Resource spring_doc_prompt;

	public ChatController(VectorStore vectorStore, ChatClient chatClient) {
		this.vectorStore = vectorStore;
		this.chatClient = chatClient;
	}

	@GetMapping(value = "/chat")
	public Flux<String> chatAssist(@RequestParam(value = "message") String message) {
		log.info("Processing Message: {}", message);
		PromptTemplate promptTemplate = new PromptTemplate(spring_doc_prompt);
		Map<String, Object> promptParameters = new HashMap<>();
		promptParameters.put("input", message);
		promptParameters.put("documents", findSimilarDocuments(message));
		Prompt prompt = promptTemplate.create(promptParameters);
		
		return chatClient.prompt(prompt).stream().content();
	}

	private List<String> findSimilarDocuments(String message) {
		List<Document> similarDocuments = vectorStore
				.similaritySearch(SearchRequest.builder().query(message).topK(1).similarityThreshold(0.5).build());
		log.info("similar documents found: {}", similarDocuments.size());
		return similarDocuments.stream().map(doc -> doc.getFormattedContent()).toList();
	}

	/**
		curl -N --get \
	  --data-urlencode "message=provide me a brief example of spring batch configuration" \
	  "http://localhost:8080/api/v1/chat"
	  */
	
}
