package mca.fincorebanking.aspect;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import mca.fincorebanking.service.AuditService;

@Aspect
@Component
public class GlobalAuditAspect {

    private final AuditService auditService;

    public GlobalAuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    // 1. Define Pointcut: Target all Service classes
    @Pointcut("execution(* mca.fincorebanking.service..*(..))")
    public void serviceLayer() {
    }

    // 2. Define Exclusion: Do NOT audit the AuditService itself (prevents infinite
    // loop)
    @Pointcut("execution(* mca.fincorebanking.service.AuditService.*(..))")
    public void auditServiceLayer() {
    }

    // 3. Advice: Run this logic AFTER any service method finishes successfully
    @AfterReturning(pointcut = "serviceLayer() && !auditServiceLayer()", returning = "result")
    public void logAfterServiceMethod(JoinPoint joinPoint, Object result) {
        try {
            // Get Current User
            String username = getCurrentUsername();

            // Get Class and Method Name
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            // Optional: Get Arguments (be careful with sensitive data like passwords!)
            // For safety, we only log the method signature in this basic version.

            // Construct Action Message
            String action = String.format("Executed: %s.%s()", className, methodName);

            // Refinement: Add details for critical actions if arguments exist
            if (joinPoint.getArgs().length > 0 && isCriticalAction(methodName)) {
                String args = Arrays.stream(joinPoint.getArgs())
                        .map(obj -> obj != null ? obj.toString() : "null")
                        .limit(1) // Limit to first argument (usually ID) to avoid noise
                        .collect(Collectors.joining(", "));
                action += " [Args: " + args + "]";
            }

            // Write to Database
            auditService.log(username, action);

        } catch (Exception e) {
            // Failsafe: Audit logging should never break the main business flow
            System.err.println("Failed to log audit: " + e.getMessage());
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "SYSTEM"; // Fallback for scheduled tasks or startup logic
    }

    // Helper to identify when to log arguments
    private boolean isCriticalAction(String methodName) {
        methodName = methodName.toLowerCase();
        return methodName.contains("save") ||
                methodName.contains("update") ||
                methodName.contains("delete") ||
                methodName.contains("approve") ||
                methodName.contains("reject") ||
                methodName.contains("block");
    }
}