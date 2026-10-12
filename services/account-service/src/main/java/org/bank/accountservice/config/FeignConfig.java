package org.bank.accountservice.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(JwtService jwtService) {

        log.info("Creating Feign RequestInterceptor for service-to-service authentication");

        return requestTemplate -> {

            log.debug(
                    "Creating service token for Feign request: method={}, url={}",
                    requestTemplate.method(),
                    requestTemplate.url()
            );

            String token = jwtService.createServiceToken("account-service");

            requestTemplate.header(
                    "Authorization",
                    "Bearer " + token
            );

            log.debug(
                    "Authorization header added to Feign request: method={}, url={}",
                    requestTemplate.method(),
                    requestTemplate.url()
            );
        };
    }
}
