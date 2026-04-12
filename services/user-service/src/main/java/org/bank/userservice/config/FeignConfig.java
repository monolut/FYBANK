package org.bank.userservice.config;

import feign.RequestInterceptor;
import org.bank.authcommon.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(JwtService jwtService) {
        return requestTemplate -> {
            String token = jwtService.generateServiceToken("user_service");
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }

}
