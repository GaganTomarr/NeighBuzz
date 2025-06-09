package com.infy.lcp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.service.FileStorageServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag; 

@RestController
@Tag(name = "File Upload", description = "APIs for uploading files/images")
public class FileUploadController {

	@Autowired
	private FileStorageServiceImpl fileStorageServiceImpl;

	@PostMapping("/uploadImage")
	@Operation(summary = "Upload an image file", description = "Uploads an image and returns the URL where it's stored")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
		@ApiResponse(responseCode = "400", description = "Invalid image file")
	})
	public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
		String fileUrl = fileStorageServiceImpl.storeFile(image);
		return new ResponseEntity<>(fileUrl, HttpStatus.OK);
	}
}
