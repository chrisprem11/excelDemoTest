package com.example.demo.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.Todo;

@Service
public class DataService {

	@Autowired
	private UploadService uploadService;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public List<Map<String, String>> doParsing(MultipartFile file) throws Exception {
		Path tempDir = Files.createTempDirectory("");
		File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
		file.transferTo(tempFile);

		Workbook workbook = WorkbookFactory.create(tempFile);
		Sheet sheet = workbook.getSheetAt(0);
		List<Map<String, String>> intermediateResults = uploadService.upload(sheet);
		intermediateResults.forEach(result -> {
			result.entrySet().forEach(item -> {
				if (item.getValue().endsWith("0")) {
					item.setValue(item.getValue().substring(0, item.getValue().length() - 2));
				}
			});
		});
		return intermediateResults;
	}

	public void storeFile(MultipartFile file) {

		try {
			String filename = file.getOriginalFilename();
			String directory = "src/main/resources/documents";
			String filepath = Paths.get(directory, filename).toString();
			System.out.println(filepath);

			// Save the file locally
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
			stream.write(file.getBytes());
			stream.close();
		} catch (Exception e) {
			System.out.println("Exception in saving file locally !");
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}
	
	public void sendWSMessage() {
		Todo todo = new Todo();
		todo.setName("WS Task");
		todo.setTask("Complete Websocket Over SockJS");
		messagingTemplate.convertAndSend("/chat", todo);
	}

}
