package org.bank.userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "account-service", url = "${account.service.url}")
public interface AccountFeign {
}
