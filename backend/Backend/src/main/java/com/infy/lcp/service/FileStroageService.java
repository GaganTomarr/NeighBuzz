package com.infy.lcp.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface FileStroageService {
	public String storeFile(MultipartFile file);
	public Path getFileStorageLocation();
}
