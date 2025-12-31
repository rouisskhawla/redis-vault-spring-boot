package com.devops.api.redis.service;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devops.api.redis.config.RedisConfig;
import com.devops.api.redis.dto.CreateDataRequest;
import com.devops.api.redis.dto.CreateDataResponse;
import com.devops.api.redis.dto.GetDataResponse;
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

	public CreateDataResponse saveDataModel(CreateDataRequest createDataRequest) {

		if (createDataRequest == null) {
			throw new IllegalArgumentException("Data model must not be null");
		}

		if (createDataRequest.id() == null || createDataRequest.id().isBlank()) {
			throw new IllegalArgumentException("ID must not be null or empty");
		}

		try {
			DataModel dataModel = new DataModel();
			dataModel.setId(createDataRequest.id());
			dataModel.setValue(JsonUtil.jsonToString(createDataRequest.jsonValue()));
			dataModel.setTtl(redisConfig.getDataModelTtl());

			DataModel saved = dataRepository.save(dataModel);

			return new CreateDataResponse(saved.getId(), saved.getTtl(), "Data model is saved to Redis");

		} catch (Exception ex) {
			logger.error("Error saving DataModel with id: {}", createDataRequest.id(), ex);
			throw ex;
		}
	}

	public GetDataResponse getDataModel(String id) {

		if (id == null || id.isBlank() || "null".equalsIgnoreCase(id)) {
			logger.warn("Received null or empty ID");
			throw new IllegalArgumentException("ID must not be null or empty");
		}

		return dataRepository.findById(id)
				.map(dataModel -> new GetDataResponse(JsonUtil.stringToJson(dataModel.getValue()))).orElseThrow(() -> {
					logger.error("Data model not found for ID: {}", id);
					return new NoSuchElementException("Data model for this ID is not found: " + id);
				});
	}
}
