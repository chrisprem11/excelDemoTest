package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.DataService;

@RestController
public class HomeController {

	@Autowired
	private DataService dataService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@GetMapping("/")
	public ResponseEntity<String> sayHello() {
		return new ResponseEntity<String>("Hello World !!", HttpStatus.OK);
	}

	@PostMapping(path = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<Map<String, String>>> getFile(@RequestPart("file") MultipartFile file) throws Exception {
		List<Map<String, String>> values = dataService.doParsing(file);
		return new ResponseEntity<List<Map<String, String>>>(values, HttpStatus.OK);
	}

	@MessageMapping("/message")
	public void testWesocket(@Payload String message) {
		messagingTemplate.convertAndSend("/chat", message);
	}
	
	@GetMapping("/test-ws")
	public ResponseEntity<String> testWS() {
		dataService.sendWSMessage();
		return new ResponseEntity<String>("Normal Response", HttpStatus.OK);
	}
}
