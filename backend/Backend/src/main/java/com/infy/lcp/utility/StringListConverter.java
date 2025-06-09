package com.infy.lcp.utility;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//
//@Converter
//public class StringListConverter implements AttributeConverter<List<String>, String>{
//
//	private static final ObjectMapper mapper = new ObjectMapper();
//	@Override
//	public String convertToDatabaseColumn(List<String> attribute) {
//		// TODO Auto-generated method stub
//		if(attribute == null || attribute.isEmpty()) {
//			return null;
//		}
//		try {
//			return mapper.writeValueAsString(attribute);
//		}
//		catch(JsonProcessingException e) {
//			throw new RuntimeException("Error parsing the JSON ", e);
//		}
//	}
//
//	@Override
//	public List<String> convertToEntityAttribute(String dbData) {
//		// TODO Auto-generated method stub
//		if(dbData == null || dbData.trim().isEmpty()) {
//			return  new ArrayList<>();
//		}
//		try {
//			if(dbData.startsWith("[") && dbData.endsWith("]")) {
//				return mapper.readValue(dbData, new TypeReference<List<String>>() {});
//			}
//			else {
//				return Arrays.asList(dbData);
//			}
////			return mapper.readValue(dbData, new TypeReference<List<String>>() {});
//		}
//		catch(JsonProcessingException e) {
//			throw new RuntimeException("JSON parsing error ", e);
////			return Arrays.asList(dbData);
//		}
//	}
//
//}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Configuration;

@Configuration
public class StringListConverter{
	
	private static final String DELIMITER = "|";
	
	public String listToString(List<String> input) {
		if(input == null || input.isEmpty()) {
			return "";
		}
		return String.join(DELIMITER, input);
	}
	
	public String mapToString(Map<Integer, String> map) {
		if(map == null || map.isEmpty()) {
			return "";
		}
		return map.entrySet().stream()
				.map(entry -> entry.getValue())
				.collect(Collectors.joining(DELIMITER));
	}
	
	public List<String> stringToList(String input){
		if(input == null || input.trim().isEmpty()) {
			return new ArrayList<>();
		}
		return Arrays.stream(input.split("\\|"))
				.map(String::trim)
				.collect(Collectors.toList());
	}
	
	public Map<Integer, String> stringToMap(String input){
		if(input == null || input.trim().isEmpty()) {
			return new HashMap<>();
		}
		Map<Integer, String> map = new HashMap<>();
		String[] output = input.split("\\|");
		for(int i =0; i<output.length;i++) {
			String parts = output[i].trim();
			if(parts.contains(":")) {
				String[] keyValue = parts.split(":", 2);
				try {
					Integer key = Integer.parseInt(keyValue[0].trim());
					String value = keyValue[1].trim();
					map.put(key,  value);
				}
				catch(NumberFormatException e) {
					map.put(i, parts);
				}
			}
		}
		return map;
	}
}