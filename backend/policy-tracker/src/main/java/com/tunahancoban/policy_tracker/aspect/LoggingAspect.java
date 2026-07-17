package com.tunahancoban.policy_tracker.aspect;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.model.entity.Customer;
import com.tunahancoban.policy_tracker.model.entity.Log;
import com.tunahancoban.policy_tracker.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    //Custom annotation for Logging
    private final LogRepository logRepository;

    @AfterReturning(pointcut = "@annotation(logActivity)",returning = "result")
    public void logExecute(JoinPoint joinPoint, LogActivity logActivity, Object result) {

        String finalDetail = logActivity.detail();

        if(result instanceof Customer){
            Customer createdCustomer = (Customer) result;
            finalDetail += " Müşteri ID: " +createdCustomer.getCustomerId();
        } else {
            //If method returns nothing catches the parameter
            Object[] methodArgs = joinPoint.getArgs();
            if (methodArgs.length > 0) {
                if (methodArgs[0] instanceof String) {
                    finalDetail += " Müşteri ID: " + methodArgs[0];
                }
            }
        }

        String userEmail = "SYSTEM";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {

            userEmail = authentication.getName();
        }


        Log log = Log.builder()
                .type(logActivity.type())
                .detail(finalDetail)
                .user(userEmail).build();
        logRepository.save(log);
    }
}