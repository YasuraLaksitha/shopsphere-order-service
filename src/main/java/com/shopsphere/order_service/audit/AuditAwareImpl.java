package com.shopsphere.order_service.audit;

import com.shopsphere.order_service.context.UserContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration(value = "auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional
                .ofNullable(UserContext.get())
                .or(() -> Optional.of("ORDER_MS"));
    }
}
