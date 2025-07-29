package com.f4ken0name.github.audit.aspects;


import com.f4ken0name.github.audit.publishers.AuditPublisher;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditPublisher consoleAuditService;
    private final AuditPublisher kafkaAuditService;

    @Value("${audit.mode:console}")
    private String auditMode;

    @Around("@annotation(weilandWatchingYou)")
    public Object auditMethod(ProceedingJoinPoint pjp, WeylandWatchingYou weilandWatchingYou) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        String params = Arrays.toString(pjp.getArgs());

        Object result;
        try {
            result = pjp.proceed();
        } catch (Throwable e) {
            audit("Exception in " + methodName + ", params=" + params + ", exception=" + e);
            throw e;
        }

        audit("Method " + methodName + " called with params " + params + ", result: " + result);
        return result;
    }

    private void audit(String message) {
        if ("kafka".equalsIgnoreCase(auditMode)) {
            kafkaAuditService.publish(message);
        } else {
            consoleAuditService.publish(message);
        }
    }
}
