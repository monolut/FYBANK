package org.bank.currencyservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.common.ConversionRequest;
import org.bank.common.ConversionResult;
import org.bank.currencyservice.service.CurrencyConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/currency")
@CrossOrigin("*")
public class CurrencyController {

    private final CurrencyConversionService currencyConversionService;

    @PostMapping
    public ResponseEntity<ConversionResult> convert(
            @RequestBody ConversionRequest request
    ) {
        return ResponseEntity.ok(
                currencyConversionService.convert(
                        request.money(),
                        request.currencyCode()
                )
        );
    }
}
