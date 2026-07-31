package com.tunahancoban.policy_tracker.repository;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InstallmentRepositoryCustomImpl implements InstallmentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Map<String, Object>> getExpectedMonthlyIncome(int year) {
        MatchOperation match = Aggregation.match(
                Criteria.where("dueDate").gte(LocalDate.of(year, 1, 1))
                        .lte(LocalDate.of(year, 12, 31))
        );

        ProjectionOperation project = Aggregation.project()
                .and("amount").as("amount")
                .and(DateOperators.dateOf("dueDate").year()).as("year")
                .and(DateOperators.dateOf("dueDate").month()).as("month");

        GroupOperation group = Aggregation.group("year", "month")
                .sum("amount").as("totalExpected")
                .count().as("installmentCount");

        ProjectionOperation flatten = Aggregation.project("totalExpected", "installmentCount")
                .and("_id.year").as("year")
                .and("_id.month").as("month");

        SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "year", "month");

        Aggregation aggregation = Aggregation.newAggregation(match, project, group, flatten, sort);

        // Document olarak dönersek Map'e çevirmesi kolaylaşır
        return mongoTemplate.aggregate(aggregation, "installments", Document.class)
                .getMappedResults()
                .stream()
                .map(doc -> (Map<String, Object>) doc)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getPaidMonthlyIncome(int year){
        return null;
    }

}