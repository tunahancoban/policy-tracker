package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.mapper.PolicyMapper;
import com.tunahancoban.policy_tracker.model.DTO.request.policy.PolicySearchRequest;
import com.tunahancoban.policy_tracker.model.indexes.PolicyIndex;
import com.tunahancoban.policy_tracker.model.entity.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicySearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final PolicyMapper policyMapper;

    public Page<Policy> search(PolicySearchRequest request) {
        Criteria criteria = buildCriteria(request);

        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(request.getSortDirection())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                request.getSortBy()
        );

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(pageRequest);

        SearchHits<PolicyIndex> hits = elasticsearchOperations.search(query, PolicyIndex.class);

        List<Policy> content = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(policyMapper::toEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageRequest, hits.getTotalHits());
    }

    private Criteria buildCriteria(PolicySearchRequest request) {
        Criteria criteria;

        if (Boolean.FALSE.equals(request.getIsActive())) {
            criteria = new Criteria("isActive").is(false);
        } else {
            criteria = new Criteria("isActive").is(true)
                    .and(new Criteria("deletedAt").exists().not());
        }

        if (request.getPolicyId() != null && !request.getPolicyId().isBlank()) {
            criteria = criteria.and(new Criteria("policyId").startsWith(request.getPolicyId()));
        }

        if (request.getCustomerId() != null) {
            criteria = criteria.and(new Criteria("customerId").is(request.getCustomerId()));
        }
        if (request.getResponsibleUserId() != null) {
            criteria = criteria.and(new Criteria("responsibleUserId").is(request.getResponsibleUserId()));
        }
        if (request.getIsActive() != null) {
            criteria = criteria.and(new Criteria("isActive").is(request.getIsActive()));
        }
        if (request.getType() != null) {
            criteria = criteria.and(new Criteria("type").is(request.getType()));
        }
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            criteria = criteria.and(new Criteria("note").contains(request.getKeyword()));
        }
        if (request.getEndDateFrom() != null) {
            criteria = criteria.and(new Criteria("endDate").greaterThanEqual(request.getEndDateFrom()));
        }
        if (request.getEndDateTo() != null) {
            criteria = criteria.and(new Criteria("endDate").lessThanEqual(request.getEndDateTo()));
        }
        if (request.getPremiumMin() != null) {
            criteria = criteria.and(new Criteria("premium").greaterThanEqual(request.getPremiumMin()));
        }
        if (request.getPremiumMax() != null) {
            criteria = criteria.and(new Criteria("premium").lessThanEqual(request.getPremiumMax()));
        }

        return criteria;
    }
}