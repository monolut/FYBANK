package org.bank.authservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.bank.authcommon.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;

        log.info("JwtAuthenticationFilter initialized");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();
        boolean skip = path.startsWith("/api/v1/auth");

        if (skip) {
            log.debug("Skipping JWT filter for path={}", path);
        }

        return skip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.debug("JWT filter processing request: method={}, path={}", method, path);

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            log.debug("Authorization header is missing, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header does not contain Bearer token");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);

            log.debug("Bearer token received, validating JWT");

            if (!jwtService.validateToken(jwt)) {
                log.warn("Invalid or expired JWT for path={}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            log.debug("JWT validation successful");

            String userId = jwtService.extractUserId(jwt);
            String role = jwtService.extractRole(jwt);
            String service = jwtService.extractService(jwt);

            log.debug(
                    "JWT claims extracted: userId={}, role={}, service={}",
                    userId,
                    role,
                    service
            );

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                Object principal;

                if (service != null) {
                    principal = service;
                    log.debug("Authenticating service principal: {}", service);
                } else {
                    principal = Long.parseLong(userId);
                    log.debug("Authenticating user principal: {}", userId);
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                jwt,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info(
                        "JWT authentication successful: userId={}, role={}, path={}",
                        userId,
                        role,
                        path
                );

            } else {
                log.debug("SecurityContext already contains authentication");
            }

        } catch (Exception e) {

            log.warn(
                    "JWT authentication failed: method={}, path={}, reason={}",
                    method,
                    path,
                    e.getMessage()
            );

            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);

        log.debug(
                "JWT filter completed: method={}, path={}, status={}",
                method,
                path,
                response.getStatus()
        );
    }
}