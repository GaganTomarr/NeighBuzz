package com.infy.lcp.filestorage;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; 
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; 
import java.nio.file.Path; 
import java.nio.file.Paths; 

@Configuration 
public class WebConfig implements WebMvcConfigurer { 
	private final Path uploadDir; 

	@Autowired 
	public WebConfig(FileStorageProperties fileStorageProperties) { 
		this.uploadDir = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath(); 
	} 

	@Override 
	public void addResourceHandlers(ResourceHandlerRegistry registry) { 
		registry.addResourceHandler("/images/**") 
		.addResourceLocations("file:" + uploadDir + "/"); 
	} 
}