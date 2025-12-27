package com.technical.commonutil;

public class SecurityUtil {

    public static String maskPassword(Object arg) {
        if (arg == null) return "null";
        String str = arg.toString();
        // Match "password=anyValue" and replace with "password=***"
        return str.replaceAll("(?i)(password=)[^,\\s)]+", "$1***");
    }
}
