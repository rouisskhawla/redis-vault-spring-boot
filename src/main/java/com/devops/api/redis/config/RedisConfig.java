package com.devops.api.redis.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;



@Configuration
@EnableRedisRepositories(basePackages = "com.devops.api.redis.repository")
public class RedisConfig {

	@Value("${redis.host}")
	private String host;

	@Value("${redis.port}")
	private int port;

	@Value("${redis.username}")
	private String username;

	@Value("${redis.password}")
	private String password;

	@Value("${redis.dataModelTtl}")
	private Long dataModelTtl;

	@Value("${redis.caCert}")
	private String caCert;

	@Bean
    LettuceConnectionFactory redisConnectionFactory() throws IOException {

		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(host);
		config.setPort(port);
		config.setUsername(username);
		config.setPassword(password);

		File caFile = File.createTempFile("redis-ca", ".crt");
		try (FileOutputStream fos = new FileOutputStream(caFile)) {
			fos.write(caCert.getBytes(StandardCharsets.UTF_8));
		}

		SslOptions sslOptions = SslOptions.builder().jdkSslProvider().trustManager(caFile).build();

		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().useSsl().and()
				.clientOptions(ClientOptions.builder().sslOptions(sslOptions).build()).build();

		return new LettuceConnectionFactory(config, clientConfig);

	}

	public Long getDataModelTtl() {
		return dataModelTtl;
	}
}
