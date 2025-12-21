package com.devops.api.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ComDevopsApiRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComDevopsApiRedisApplication.class, args);
	}

}
