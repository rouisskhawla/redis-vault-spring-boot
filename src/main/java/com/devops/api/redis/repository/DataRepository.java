package com.devops.api.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.devops.api.redis.model.DataModel;

@Repository
public interface DataRepository extends CrudRepository<DataModel, String> {
}