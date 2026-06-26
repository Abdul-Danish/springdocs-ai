package com.danish.springrag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

	@Bean
	public ChatClient chatClient(ChatModel chatModel) {
		return ChatClient.create(chatModel);
	}

}
