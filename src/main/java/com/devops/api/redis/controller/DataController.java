package com.devops.api.redis.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devops.api.redis.DataService;
import com.devops.api.redis.model.DataModel;
import com.devops.api.redis.model.RequestModel;

@RestController
@RequestMapping("/api/data")
public class DataController {

	@Autowired
	private DataService dataService;

	@PostMapping
	public ResponseEntity<DataModel> createDataModel(@RequestBody RequestModel requestModel) {
		DataModel savedModel = dataService.saveDataModel(requestModel.getId(), requestModel.getJsonValue());
		return ResponseEntity.ok(savedModel);
	}



	@GetMapping("/{id}")
	public ResponseEntity<String> getDataModel(@PathVariable String id) {
		try {
			String requestModel = dataService.getDataModel(id);
			return ResponseEntity.ok(requestModel);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

}

