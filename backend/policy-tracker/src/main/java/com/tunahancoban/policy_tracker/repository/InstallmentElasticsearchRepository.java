package com.tunahancoban.policy_tracker.repository;

import com.tunahancoban.policy_tracker.model.indexes.InstallmentIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface InstallmentElasticsearchRepository extends ElasticsearchRepository<InstallmentIndex, String> {
}
