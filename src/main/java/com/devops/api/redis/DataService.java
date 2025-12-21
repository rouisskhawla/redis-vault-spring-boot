package com.devops.api.redis;

import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devops.api.redis.config.RedisConfig;
import com.devops.api.redis.model.DataModel;
import com.devops.api.redis.repository.DataRepository;
import com.devops.api.redis.utility.JsonUtil;

@Service
public class DataService {

	private static final Logger logger = LoggerFactory.getLogger(DataService.class);

	@Autowired
	private DataRepository dataRepository;
	@Autowired
	private RedisConfig redisConfig;

	public DataModel saveDataModel(String id, Map<String, Object> value) {
		try {
			DataModel dataModel = new DataModel();
			String stringValue = JsonUtil.jsonToString(value);
			dataModel.setId(id);
			dataModel.setValue(stringValue);
			dataModel.setTtl(redisConfig.getDataModelTtl());

			return dataRepository.save(dataModel);
		} catch (Exception e) {
			logger.error("Error saving dataModel", e);
			return null;
		}

	}

	public String getDataModel(String id) {
		if (id == null || id.isBlank() || id.equals("null")) {
            logger.warn("Received null or empty ID");
			throw new IllegalArgumentException("ID must not be null or empty");
		}

		return dataRepository.findById(id).map(dataModel -> {
			return dataModel.getValue();

		}).orElseThrow(() -> {

			logger.error("Data model not found for ID: {}", id);
			return new NoSuchElementException("Data model for this ID is not found: " + id);
		});
	}

}

