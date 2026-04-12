package org.bank.authcommon.filter;

import org.bank.authcommon.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request){
        boolean skip = request.getServletPath().startsWith("/api/v1/auth");

        if(skip){
            log.debug("Skipping JWT filter for path={}", request.getServletPath());
        }

        return skip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);

            if (!jwtService.validateToken(jwt)) {
                log.warn("Invalid or expired JWT");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String userId = jwtService.extractUserId(jwt);
            String role = jwtService.extractRole(jwt);
            String service = jwtService.extractService(jwt);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                Object principal;

                if (service != null) {
                    principal = service;
                    log.debug("Authenticated service: {}", service);
                } else {
                    principal = Long.parseLong(userId);
                    log.debug("Authenticated user: {}", userId);
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

                log.debug("JWT authentication successful, userId={}, role={}", userId, role);
            }
        } catch (Exception e) {

            log.warn(
                    "JWT authentication failed, path={}, reason={}",
                    request.getRequestURI(),
                    e.getMessage()
            );
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
