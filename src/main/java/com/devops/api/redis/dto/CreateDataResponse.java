package com.devops.api.redis.dto;

public record CreateDataResponse(String id, Long ttl, String message) {

}
