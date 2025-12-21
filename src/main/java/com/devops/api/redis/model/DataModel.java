package com.devops.api.redis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("dataModel")
public class DataModel {

	@Id
	private String id;

	private String value;

	@TimeToLive
	private Long ttl;

}
