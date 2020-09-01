package com.template.utils;

public class CommonUtils {
    private static final String ALPHA_NUMERIC_STRING = "0123456789";
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static Double calculateEMI(int interestRate, int period, Double amount) {
        Double rate = interestRate * 0.00084;
        int time = period * 12;

        Double monthlyEMI = amount * rate / (1 - (Math.pow(1/(1 + rate), time)));
        return  monthlyEMI;
    }
}
