package com.granddukenick.duitlater.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Order(1)
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    private static final int MAX_PAYLOAD_LENGTH = 10000;
    private static final List<String> SENSITIVE_HEADERS = Arrays.asList(
            "authorization", "cookie", "set-cookie", "x-auth-token"
    );
    private static final List<String> SENSITIVE_FIELDS = Arrays.asList(
            "password", "token", "secret", "key", "pwd_hsh", "accessToken"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            logRequest(wrappedRequest, requestId);

            chain.doFilter(wrappedRequest, wrappedResponse);

            logResponse(wrappedResponse, requestId, startTime);

        } catch (Exception e) {
            log.error("REQUEST_ID={} | Exception: {}", requestId, e.getMessage(), e);
            throw e;
        } finally {
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, String requestId) {
        try {
            StringBuilder logBuilder = new StringBuilder();

            logBuilder.append("\n");
            logBuilder.append("========== REQUEST ==========\n");
            logBuilder.append("REQUEST_ID=").append(requestId).append("\n");
            logBuilder.append("TIMESTAMP=").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
            logBuilder.append("METHOD=").append(request.getMethod()).append("\n");
            logBuilder.append("URI=").append(request.getRequestURI()).append("\n");
            logBuilder.append("QUERY_STRING=").append(request.getQueryString() != null ? request.getQueryString() : "").append("\n");
            logBuilder.append("REMOTE_ADDR=").append(request.getRemoteAddr()).append("\n");
            logBuilder.append("USER_AGENT=").append(request.getHeader("User-Agent")).append("\n");

            // Headers (masked)
            logBuilder.append("HEADERS=").append(maskSensitiveHeaders(getHeaders(request))).append("\n");

            // Request body
            String requestBody = getRequestBody(request);
            if (requestBody != null && !requestBody.isEmpty()) {
                logBuilder.append("BODY=").append(maskSensitiveFields(truncate(requestBody))).append("\n");
            }

            logBuilder.append("========== REQUEST END ==========");

            log.info(logBuilder.toString());

        } catch (Exception e) {
            log.warn("Failed to log request: {}", e.getMessage());
        }
    }

    private void logResponse(ContentCachingResponseWrapper response, String requestId, long startTime) {
        try {
            long duration = System.currentTimeMillis() - startTime;
            StringBuilder logBuilder = new StringBuilder();

            logBuilder.append("\n");
            logBuilder.append("========== RESPONSE ==========\n");
            logBuilder.append("REQUEST_ID=").append(requestId).append("\n");
            logBuilder.append("STATUS=").append(response.getStatus()).append("\n");
            logBuilder.append("DURATION_MS=").append(duration).append("\n");

            // Response body
            String responseBody = getResponseBody(response);
            if (responseBody != null && !responseBody.isEmpty()) {
                logBuilder.append("BODY=").append(maskSensitiveFields(truncate(responseBody))).append("\n");
            }

            logBuilder.append("========== RESPONSE END ==========");

            // Log level based on status
            if (response.getStatus() >= 500) {
                log.error(logBuilder.toString());
            } else if (response.getStatus() >= 400) {
                log.warn(logBuilder.toString());
            } else {
                log.info(logBuilder.toString());
            }

        } catch (Exception e) {
            log.warn("Failed to log response: {}", e.getMessage());
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return null;
        }
        return new String(content, StandardCharsets.UTF_8);
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) {
            return null;
        }
        return new String(content, StandardCharsets.UTF_8);
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }

    private Map<String, String> maskSensitiveHeaders(Map<String, String> headers) {
        Map<String, String> masked = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (SENSITIVE_HEADERS.contains(key)) {
                masked.put(entry.getKey(), "[MASKED]");
            } else {
                masked.put(entry.getKey(), entry.getValue());
            }
        }
        return masked;
    }

    private String maskSensitiveFields(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        String masked = content;
        for (String field : SENSITIVE_FIELDS) {
            // JSON pattern: "fieldName":"value" or "fieldName": "value"
            String pattern = "(?i)(\"" + field + "\"\\s*:\\s*\")([^\"]*)(\")";
            masked = masked.replaceAll(pattern, "$1[MASKED]$3");
        }
        return masked;
    }

    private String truncate(String content) {
        if (content == null) {
            return null;
        }
        if (content.length() <= MAX_PAYLOAD_LENGTH) {
            return content;
        }
        return content.substring(0, MAX_PAYLOAD_LENGTH) + "... [TRUNCATED]";
    }
}