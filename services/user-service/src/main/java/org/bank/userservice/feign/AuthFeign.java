package org.bank.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "auth-service",
        url = "http://localhost:8080/api/v1/auth"
)
public interface AuthFeign {
    @PostMapping("/logoutAll")
    ResponseEntity<Void> logoutAll();

    @DeleteMapping("/internal/delete")
    ResponseEntity<Void> deleteUserData();
}
