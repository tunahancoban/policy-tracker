package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.indexes.PolicyIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PolicyElasticsearchRepository extends ElasticsearchRepository<PolicyIndex, String> {
}