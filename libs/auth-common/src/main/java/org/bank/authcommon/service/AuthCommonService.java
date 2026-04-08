package org.bank.authcommon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class AuthCommonService {

    private final JwtService jwtService;

    @Autowired
    public AuthCommonService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Long getUserId() {
        Authentication authentication = checkAuthentication();

        return Long.parseLong(authentication.getPrincipal().toString());
    }

    public String getUserEmail() {
        Authentication authentication = checkAuthentication();

        return jwtService.extractEmail(authentication.getCredentials().toString());
    }

    private Authentication checkAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("User is not authenticated");
        }

        return authentication;
    }
}
