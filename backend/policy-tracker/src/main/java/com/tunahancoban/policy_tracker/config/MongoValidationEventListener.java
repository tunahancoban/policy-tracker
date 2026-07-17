package com.tunahancoban.policy_tracker.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class MongoValidationEventListener  extends AbstractMongoEventListener<Object> {

    private final Validator validator;

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event){
        Object source = event.getSource();

        Set<ConstraintViolation<Object>> violations = validator.validate(source);

        if(!violations.isEmpty()){
            throw new ConstraintViolationException((violations));
        }
    }


}
