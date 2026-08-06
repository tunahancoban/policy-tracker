package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.entity.DatabaseSequence;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import com.tunahancoban.policy_tracker.service.interfaces.IdGeneratorServiceImp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdGeneratorService implements IdGeneratorServiceImp {

    private final MongoOperations mongoOperations;

    @Override
    public long getNextSequence(String seqName) {
        log.debug("Fetching next sequence value for: {}", seqName);

        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1);
        DatabaseSequence counter = mongoOperations.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true).upsert(true), DatabaseSequence.class);

        if (Objects.isNull(counter)) {
            log.warn("Sequence counter returned null for: {}, falling back to 1", seqName);
            return 1;
        }

        log.debug("Sequence value obtained - name: {}, value: {}", seqName, counter.getSeq());
        return counter.getSeq();
    }

    @Override
    public String generateCustomerId() {
        String sequenceName = "customer_sequence";
        long currentSeq = getNextSequence(sequenceName);
        String customerId = String.format("CST-%06d", currentSeq);

        log.info("Generated new customer ID: {}", customerId);
        return customerId;
    }

    @Override
    public String generatePolicyId(PolicyType type) {
        String prefix = type.getPrefix();
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();
        String sequenceName = String.format("%s_%d_%02d_sequence", prefix, currentYear, currentMonth);

        long currentSeq = getNextSequence(sequenceName);
        String policyId = String.format("%s%d%02d%04d", prefix, currentYear, currentMonth, currentSeq);

        log.info("Generated new policy ID: {} for type: {}", policyId, type);
        return policyId;
    }


}