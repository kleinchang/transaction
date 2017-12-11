package com.commonwealth.banking.util;


public class Utils {
    public static String formatDollar(String amount) {
        if (amount.startsWith("-"))
            return String.format("-$%s", amount.substring(1));
        else
            return String.format("$%s", amount);
    }
}
