package com.danish.springrag.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.danish.springrag.service.ChatService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ChatController {

	private final ChatService chatService;
	

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@GetMapping(value = "/chat")
	public Flux<String> chatAssist(@RequestParam(value = "message") String message) {
		return chatService.chat(message);
	}
	

	/**
		curl -N --get \
	  --data-urlencode "message=provide me a brief example of spring batch configuration" \
	  "http://localhost:8080/api/v1/chat"
	  */
	
}
