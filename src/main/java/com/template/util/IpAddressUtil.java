package com.template.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class IpAddressUtil {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "X-Real-IP",
            "X-Client-IP",
            "X-Forwarded",
            "X-Cluster-Client-IP",
            "Forwarded-For",
            "Forwarded",
            "CF-Connecting-IP", // Cloudflare
            "True-Client-IP", // Akamai
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * Extract the real IP address from the request, considering various proxy
     * headers
     * 
     * @param request The HTTP request
     * @return The real IP address
     */
    public String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1"; // Default to localhost instead of unknown
        }

        // Check each header for IP address
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (isValidIpAddress(ip)) {
                log.debug("Found IP address in header {}: {}", header, ip);
                return extractFirstIp(ip);
            }
        }

        // Fallback to remote address
        String remoteAddr = request.getRemoteAddr();
        if (isValidIpAddress(remoteAddr)) {
            log.debug("Using remote address: {}", remoteAddr);
            return remoteAddr;
        }

        // Handle common development scenarios
        if (remoteAddr != null) {
            // IPv6 localhost
            if ("0:0:0:0:0:0:0:1".equals(remoteAddr) || "::1".equals(remoteAddr)) {
                log.debug("Using localhost IP for IPv6 address: {}", remoteAddr);
                return "127.0.0.1";
            }
            // If it's not empty but not valid IPv4, return localhost for development
            if (!remoteAddr.trim().isEmpty()) {
                log.debug("Using localhost IP for non-standard remote address: {}", remoteAddr);
                return "127.0.0.1";
            }
        }

        log.warn("Could not determine client IP address, using localhost for development");
        return "127.0.0.1"; // Default to localhost for development
    }

    /**
     * Extract the first IP address from a comma-separated list (common in
     * X-Forwarded-For)
     * 
     * @param ipList Comma-separated list of IP addresses
     * @return The first valid IP address
     */
    private String extractFirstIp(String ipList) {
        if (!StringUtils.hasText(ipList)) {
            return "unknown";
        }

        String[] ips = ipList.split(",");
        for (String ip : ips) {
            String trimmedIp = ip.trim();
            if (isValidIpAddress(trimmedIp)) {
                return trimmedIp;
            }
        }

        return "unknown";
    }

    /**
     * Validate if the given string is a valid IP address
     * 
     * @param ip The IP address to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidIpAddress(String ip) {
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }

        // Basic IPv4 validation
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Get a rate limit key based on IP address and operation type
     * 
     * @param request   The HTTP request
     * @param operation The operation type (e.g., "otp", "login", "register")
     * @return A unique key for rate limiting
     */
    public String getRateLimitKey(HttpServletRequest request, String operation) {
        String ip = getClientIpAddress(request);
        return String.format("%s:%s", operation, ip);
    }
}