package org.bank.accountservice.service;

import java.math.BigInteger;

public class IbanGenerator {

    public static String generateIban(String countryCode) {
        String bban = generateRandomBban();
        String rearranged = bban + countryCode + "00";
        String numeric = changeLetters(rearranged);
        BigInteger mod = new BigInteger(numeric).mod(BigInteger.valueOf(97));
        int checkDigits = 98 - mod.intValue();
        return countryCode + String.format("%02d", checkDigits) + bban;
    }

    private static String generateRandomBban() {
        String bankCode = String.format("%08d", (int) (Math.random() * 100_000_000));
        String accountNumber = String.format("%010d", (int) (Math.random() * 1_000_000_000));
        return bankCode + accountNumber;
    }

    private static String changeLetters(String input) {
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
