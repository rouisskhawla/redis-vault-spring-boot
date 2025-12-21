package com.devops.api.redis.utility;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;



public class JsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private JsonUtil() {
		super();
	}

	public static String jsonToString(Map<String, Object> jsonValue) {
		try {
			return objectMapper.writeValueAsString(jsonValue);
		} catch (JsonProcessingException e) {
			logger.error("Failed To Convert From Json To String", e);
			return null;
		}
	}

	public static Map<String, Object> stringToJson(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonMappingException e) {
			logger.error("Failed To Convert From String To Json", e);
			return new HashMap<>();
		} catch (JsonProcessingException e) {
			logger.error("Invalid JSON string", e);
			return new HashMap<>();
		}
	}

}
