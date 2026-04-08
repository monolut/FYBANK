package org.bank.userservice.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if(attributes == null) return;

            var request = attributes.getRequest();

            String authorization = request.getHeader("Authorization");
            if (authorization != null) {
                requestTemplate.header("Authorization", authorization);
            }
        };
    }

}
