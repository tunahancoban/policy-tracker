package com.tunahancoban.policy_tracker.aspect;

import com.tunahancoban.policy_tracker.annotation.LogActivity;
import com.tunahancoban.policy_tracker.model.entity.Log;
import com.tunahancoban.policy_tracker.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final LogRepository logRepository;
    private final ExpressionParser parser = new SpelExpressionParser();

    @AfterReturning(pointcut = "@annotation(logActivity)", returning = "result")
    public void logExecute(JoinPoint joinPoint, LogActivity logActivity, Object result) {

        String finalDetail = parseSpelDetail(joinPoint, logActivity.detail(), result);

        String userEmail = getCurrentUserEmail();

        Log log = Log.builder()
                .type(logActivity.type())
                .detail(finalDetail)
                .user(userEmail)
                .build();

        logRepository.save(log);
    }

    private String parseSpelDetail(JoinPoint joinPoint, String detailSpel, Object result) {
        if (detailSpel == null || detailSpel.isBlank()) {
            return "";
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        context.setVariable("result", result);

        try {
            return parser.parseExpression(detailSpel).getValue(context, String.class);
        } catch (Exception e) {
            return detailSpel;
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return "SYSTEM";
    }
}