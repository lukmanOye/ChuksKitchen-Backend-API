package com.example.trueminds.util;

public class LuhnValidator {
    public static boolean isValid(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) return false;
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
}