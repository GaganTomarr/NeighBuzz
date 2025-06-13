package com.infy.lcp.filestorage;

import org.springframework.boot.context.properties.ConfigurationProperties; 
import org.springframework.stereotype.Component; 

@Component
@ConfigurationProperties("file") 
public class FileStorageProperties { 
	
	private String uploadDir; 

	public String getUploadDir() { 
		return uploadDir; 
	} 

	public void setUploadDir(String uploadDir) { 
		this.uploadDir = uploadDir; 
	} 
}