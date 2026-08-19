package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.indexes.UserIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserElasticsearchRepository extends ElasticsearchRepository<UserIndex, String> {
}
