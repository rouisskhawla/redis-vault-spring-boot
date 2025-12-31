package com.devops.api.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devops.api.redis.service.DataService;
import com.devops.api.redis.dto.CreateDataRequest;
import com.devops.api.redis.dto.CreateDataResponse;
import com.devops.api.redis.dto.GetDataResponse;

@RestController
@RequestMapping("/api/data")
public class DataController {

	@Autowired
	private DataService dataService;

	@PostMapping
	public ResponseEntity<CreateDataResponse> createDataModel(@RequestBody CreateDataRequest createDataRequest) {
		var savedModel = dataService.saveDataModel(createDataRequest);
		return ResponseEntity.ok(savedModel);
	}

	@GetMapping("/{id}")
	public ResponseEntity<GetDataResponse> getDataModel(@PathVariable String id) {
		var requestModel = dataService.getDataModel(id);
		return ResponseEntity.ok(requestModel);

	}

}
