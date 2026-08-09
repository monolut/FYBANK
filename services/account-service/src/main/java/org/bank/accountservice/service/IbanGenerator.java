package org.bank.accountservice.service;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class IbanGenerator {

    public static String generateIban(String countryCode) {

        log.debug("Generating IBAN for countryCode={}", countryCode);

        String bban = generateRandomBban();

        log.debug("BBAN generated for countryCode={}", countryCode);

        String rearranged = bban + countryCode + "00";
        String numeric = changeLetters(rearranged);

        BigInteger mod = new BigInteger(numeric)
                .mod(BigInteger.valueOf(97));

        int checkDigits = 98 - mod.intValue();

        log.debug(
                "IBAN check digits calculated for countryCode={}",
                countryCode
        );

        return countryCode
                + String.format("%02d", checkDigits)
                + bban;
    }

    private static String generateRandomBban() {

        log.debug("Generating random BBAN");

        String bankCode = String.format(
                "%08d",
                (int) (Math.random() * 100_000_000)
        );

        String accountNumber = String.format(
                "%010d",
                (int) (Math.random() * 1_000_000_000)
        );

        return bankCode + accountNumber;
    }

    private static String changeLetters(String input) {

        log.debug("Converting IBAN letters to numeric representation");

        StringBuilder sb = new StringBuilder();

        for (char ch : input.toCharArray()) {
            if (Character.isLetter(ch)) {
                sb.append((int) ch - 55);
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }
}