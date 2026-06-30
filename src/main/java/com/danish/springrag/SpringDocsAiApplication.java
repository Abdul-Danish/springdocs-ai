package com.danish.springrag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SpringDocsAiApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringDocsAiApplication.class, args);
	}

	@Autowired
	@Qualifier("qwenChatClient")
	private ChatClient qwenChatClient;
	
	@Override
	public void run(String... args) throws Exception {
		qwenChatClient.prompt().user("Reply with only the word: READY").call().content();
		log.info("Qwen warmed up.");
	}

}
