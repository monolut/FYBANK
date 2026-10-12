package org.bank.transactionservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class TransactionReferenceGenerator {

    public String generate() {

        log.debug("Generating transaction reference");

        return UUID.randomUUID().toString();
    }
}