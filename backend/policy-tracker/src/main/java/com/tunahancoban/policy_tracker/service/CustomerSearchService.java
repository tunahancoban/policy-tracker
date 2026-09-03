package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.mapper.CustomerMapper;
import com.tunahancoban.policy_tracker.model.DTO.request.customer.CustomerSearchRequest;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.model.indexes.CustomerIndex;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final CustomerMapper customerMapper;

    public Page<Customer> search(CustomerSearchRequest request) {
        log.debug("Customer search initiated via ES with request: {}", request);

        Criteria criteria = buildCriteria(request);

        String sortField = resolveEsSortField(
                request.getSortBy() != null && !request.getSortBy().isBlank() ? request.getSortBy() : "customerId"
        );
        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortField
        );

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(pageRequest);

        SearchHits<CustomerIndex> hits = elasticsearchOperations.search(query, CustomerIndex.class);

        List<Customer> content = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(customerMapper::toEntity)
                .toList();

        log.debug("Customer ES search completed - total records found: {}", hits.getTotalHits());

        return new PageImpl<>(content, pageRequest, hits.getTotalHits());
    }

    private Criteria buildCriteria(CustomerSearchRequest request) {
        Criteria criteria = new Criteria();

        // Exact matches
        if (request.getCustomerId() != null && !request.getCustomerId().isBlank()) {
            criteria = criteria.and(new Criteria("customerId").is(request.getCustomerId()));
        }
        if (request.getIdentityNumber() != null && !request.getIdentityNumber().isBlank()) {
            criteria = criteria.and(new Criteria("identityNumber").is(request.getIdentityNumber()));
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            criteria = criteria.and(new Criteria("email").is(request.getEmail()));
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            criteria = criteria.and(new Criteria("phoneNumber").is(request.getPhoneNumber()));
        }
        if (request.getCity() != null && !request.getCity().isBlank()) {
            criteria = criteria.and(new Criteria("city").is(request.getCity()));
        }
        if (request.getDistrict() != null && !request.getDistrict().isBlank()) {
            criteria = criteria.and(new Criteria("district").is(request.getDistrict()));
        }
        if (request.getIsActive() != null) {
            criteria = criteria.and(new Criteria("isActive").is(request.getIsActive()));
        }

        // Containing / Text matches
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            criteria = criteria.and(new Criteria("fullName").contains(request.getFullName()));
        }
        // Global Keyword search across name & email
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            Criteria keywordCriteria = new Criteria("fullName").contains(request.getKeyword())
                    .or(new Criteria("email").contains(request.getKeyword()));
            criteria = criteria.and(keywordCriteria);
        }

        // Date range filters
        if (request.getCreatedDateFrom() != null) {
            criteria = criteria.and(new Criteria("createdAt").greaterThanEqual(request.getCreatedDateFrom()));
        }
        if (request.getCreatedDateTo() != null) {
            criteria = criteria.and(new Criteria("createdAt").lessThanEqual(request.getCreatedDateTo()));
        }

        return criteria;
    }

    /**
     * Elasticsearch'te `text` tipindeki alanlara doğrudan sort uygulanamaz.
     * Bu metot, bilinen text alanları için `.keyword` sub-field'ına yönlendirir.
     */
    private String resolveEsSortField(String requestedField) {
        return switch (requestedField) {
            case "fullName" -> "fullName.keyword";
            default -> requestedField;
        };
    }
}