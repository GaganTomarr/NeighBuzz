package com.infy.lcp.service;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import org.springframework.util.StringUtils; 
import org.springframework.web.multipart.MultipartFile;

import com.infy.lcp.filestorage.FileStorageProperties;

import java.io.IOException; 
import java.io.InputStream; 
import java.nio.file.Files; 
import java.nio.file.Path; 
import java.nio.file.Paths; 
import java.nio.file.StandardCopyOption; 
import java.util.UUID; 

@Service 
public class FileStorageServiceImpl implements FileStroageService{ 
	private final Path fileStorageLocation; 

	@Autowired 
	public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) { 
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()) 
				.toAbsolutePath().normalize(); 
		try { 
			Files.createDirectories(this.fileStorageLocation); 
		} catch (Exception ex) { 
			throw new RuntimeException("service.FILE_STORAGE", ex); 
		} 
	} 

	public String storeFile(MultipartFile file) { 
		String fileName = StringUtils.cleanPath(file.getOriginalFilename()); 
		try { 
			if (fileName.contains("..")) { 
				throw new RuntimeException("service.FILE_INVALID_NAME" + fileName); 
			} 
			String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
			Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName); 
			try (InputStream inputStream = file.getInputStream()) { 
				Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING); 
			} 
			String fileDownloadUri = "/images/" + uniqueFileName; 
			return fileDownloadUri; 
		} catch (IOException ex) { 
			throw new RuntimeException("service.FILE_RUNTIME", ex); 
		} 
	}
	
	public Path getFileStorageLocation() { 
		return fileStorageLocation; 
	} 
}