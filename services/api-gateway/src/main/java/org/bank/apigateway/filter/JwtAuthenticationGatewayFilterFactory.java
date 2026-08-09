package org.bank.apigateway.filter;

import io.jsonwebtoken.JwtException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.JwtService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    public JwtAuthenticationGatewayFilterFactory(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String path = exchange.getRequest().getPath().value();
            String method = exchange.getRequest().getMethod().name();

            log.info("Incoming request: method={}, path={}", method, path);

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null) {
                log.warn("Request rejected: Authorization header missing, path={}", path);
                return unauthorized(exchange);
            }

            if (!authHeader.startsWith("Bearer ")) {
                log.warn("Request rejected: invalid Authorization scheme, path={}", path);
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);

            try {
                jwtService.validateToken(token);

                log.debug("JWT validated successfully, path={}", path);

                return chain.filter(exchange);

            } catch (JwtException e) {

                log.warn("Request rejected: invalid JWT, path={}, reason={}",
                        path, e.getMessage());

                return unauthorized(exchange);
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {

        log.warn("Returning 401 Unauthorized, path={}",
                exchange.getRequest().getPath());

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    @Getter
    @Setter
    public static class Config {
    }
}