package com.devops.api.redis.dto;

import java.util.Map;

public record CreateDataRequest(String id, Map<String, Object> jsonValue) {
	@Override
	public String toString() {
		return "DataModelDto [id=" + id + ", jsonValue=" + jsonValue + "]";
	}
}
