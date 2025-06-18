package com.shopsphere.order_service.config;

import com.shopsphere.order_service.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class ForwardFeignInterceptor implements RequestInterceptor {

    public static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            final String userId = attributes.getRequest().getHeader(USER_ID_HEADER);
            log.debug("{} header found in ForwardFeignInterceptor: {}", USER_ID_HEADER, userId);

            if (StringUtils.hasText(userId)) {
                requestTemplate.header(USER_ID_HEADER, userId);
                return;
            }

            requestTemplate.header(USER_ID_HEADER, UserContext.get());
            log.debug("{} header injected in ForwardFeignInterceptor: {}", USER_ID_HEADER, UserContext.get());
        }

    }
}
