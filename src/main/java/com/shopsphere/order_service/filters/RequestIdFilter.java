package com.shopsphere.order_service.filters;

import com.shopsphere.order_service.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            final String userId = request.getHeader(USER_ID_HEADER);

            if (StringUtils.hasText(userId)) {
                log.debug("{} header found in RequestIdFilter: {}", USER_ID_HEADER, userId);
                UserContext.set(userId);
            }

            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
