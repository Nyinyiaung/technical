package com.technical.commonutil;

import jakarta.servlet.http.HttpServletRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {

    public static final String ACCESS_TOKEN_TYPE = "LOGIN";
    public static final String RESET_TOKEN_TYPE = "RESET";
    public static final String VERIFICATION_TOKEN_TYPE = "VERIFICATION";
    public static final String REFRESH_TOKEN_TYPE = "REFRESH";

    public static String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    public static String generateDeviceID(String userAgent, String clientIP) {
        try {
            String deviceFingerprint = (userAgent + clientIP).toLowerCase();
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(deviceFingerprint.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.substring(0, 16); // First 16 chars
        } catch (NoSuchAlgorithmException e) {
            return "unknown-device";
        }
    }
}
