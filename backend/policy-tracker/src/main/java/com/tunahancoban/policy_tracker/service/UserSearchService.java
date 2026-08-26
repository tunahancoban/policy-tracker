package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.mapper.UserMapper;
import com.tunahancoban.policy_tracker.model.DTO.request.user.UserSearchRequest;
import com.tunahancoban.policy_tracker.model.entity.User;
import com.tunahancoban.policy_tracker.model.indexes.UserIndex;
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
public class UserSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final UserMapper userMapper;

    public Page<User> search(UserSearchRequest request) {
        log.debug("User search initiated via ES with request: {}", request);

        Criteria criteria = buildCriteria(request);

        String sortField = resolveSortField(request.getSortBy());
        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortField
        );

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), sort);

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(pageRequest);

        SearchHits<UserIndex> hits = elasticsearchOperations.search(query, UserIndex.class);

        List<User> content = hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(userMapper::toEntity)
                .toList();

        log.debug("User ES search completed - total records found: {}", hits.getTotalHits());

        return new PageImpl<>(content, pageRequest, hits.getTotalHits());
    }

    private Criteria buildCriteria(UserSearchRequest request) {
        Criteria criteria = new Criteria();

        if (request.getIsActive() != null) {
            criteria = criteria.and(new Criteria("isActive").is(request.getIsActive()));
        } else {
            criteria = criteria.and(new Criteria("isActive").is(true));
        }

        if (request.getId() != null && !request.getId().isBlank()) {
            criteria = criteria.and(new Criteria("id").is(request.getId()));
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            criteria = criteria.and(new Criteria("email").is(request.getEmail()));
        }
        if (request.getRole() != null) {
            criteria = criteria.and(new Criteria("role").is(request.getRole().name()));
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            criteria = criteria.and(new Criteria("fullName").contains(request.getFullName()));
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            String keyword = request.getKeyword().trim();
            Criteria keywordCriteria = new Criteria("fullName").contains(keyword)
                    .or(new Criteria("email").contains(keyword));
            criteria = criteria.and(keywordCriteria);
        }

        return criteria;
    }

    private String resolveSortField(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "fullName.keyword";
        }
        return switch (sortBy) {
            case "fullName" -> "fullName.keyword";
            default -> sortBy;
        };
    }
}