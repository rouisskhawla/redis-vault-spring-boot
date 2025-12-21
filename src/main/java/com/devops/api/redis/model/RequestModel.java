package com.devops.api.redis.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestModel {
	
	private String id;

	private Map<String, Object> jsonValue;

	@Override
	public String toString() {
		return "RequestModel [id=" + id + ", jsonValue=" + jsonValue + "]";
	}
	
	private Long ttl;
}
