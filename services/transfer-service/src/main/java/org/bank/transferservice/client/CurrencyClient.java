package org.bank.transferservice.client;

import org.bank.common.ConversionRequest;
import org.bank.common.ConversionResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "currency-service",
        url = "${clients.currency-service.url}"
)
public interface CurrencyClient {

    @PostMapping
    ConversionResult convert(
            @RequestBody ConversionRequest request
    );
}
