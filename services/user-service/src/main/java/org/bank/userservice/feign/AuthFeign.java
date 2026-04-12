package org.bank.userservice.feign;

import org.bank.userservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "auth-service",
        url = "http://localhost:8080/api/v1/auth",
        configuration = FeignConfig.class
)
public interface AuthFeign {
    @PostMapping("/{userId}/logoutAll")
    ResponseEntity<Void> logoutAll(
            @PathVariable Long userId
    );

    @DeleteMapping("/internal/{userId}/delete")
    ResponseEntity<Void> deleteUserData(
            @PathVariable Long userId
    );
}
