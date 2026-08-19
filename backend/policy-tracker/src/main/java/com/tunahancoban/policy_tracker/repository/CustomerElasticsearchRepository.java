package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.indexes.CustomerIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CustomerElasticsearchRepository extends ElasticsearchRepository<CustomerIndex, String> {
}
