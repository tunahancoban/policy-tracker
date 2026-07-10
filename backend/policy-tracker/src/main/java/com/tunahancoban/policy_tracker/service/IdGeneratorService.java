package com.tunahancoban.policy_tracker.service;

import com.tunahancoban.policy_tracker.model.entity.DatabaseSequence;
import com.tunahancoban.policy_tracker.model.enums.PolicyType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IdGeneratorService {
    private final MongoOperations mongoOperations;

    public long getNextSequence(String seqName){
        Query query = new Query(Criteria.where("_id").is(seqName));

        Update update = new Update().inc("seq", 1);

        DatabaseSequence counter = mongoOperations.findAndModify(query,update, FindAndModifyOptions.options().returnNew(true).upsert(true),DatabaseSequence.class);

        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

    public String generateCustomerId() {
        String sequenceName = "customer_sequence";
        long currentSeq = getNextSequence(sequenceName);

        return String.format("CST-%06d", currentSeq);
    }

    public String generatePolicyId(PolicyType type) {
        String prefix = getPolicyPrefix(type);
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        String sequenceName = String.format("%s_%d_%02d_sequence", prefix, currentYear, currentMonth);
        long currentSeq = getNextSequence(sequenceName);

        return String.format("%s%d%02d%04d", prefix, currentYear, currentMonth, currentSeq);
    }

    private String getPolicyPrefix(PolicyType type) {
        switch (type) {
            case TRAFIK: return "TRF";
            case KASKO: return "KSK";
            case DASK: return "DSK";
            case KONUT: return "KNT";
            case SAGLIK: return "SGL";
            default: return "POL";
        }
    }
}
