package ru.shalkan.loggingstarter.webfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.shalkan.loggingstarter.util.MaskingHelper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WebLoggingFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingFilter.class);

    @Autowired
    private MaskingHelper maskingHelper;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);
        String headers = inlineHeaders(request);

        log.info("Запрос: {} {} {}", method, requestURI, headers);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            super.doFilter(request, responseWrapper, chain);
            String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            log.info("Ответ: {} {} {} {}", method, requestURI, response.getStatus(), maskingHelper.prepareMaskedBody(responseBody));
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private String inlineHeaders(HttpServletRequest request) {
        Map<String, Object> headersMap = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(it -> it, request::getHeader));
        String headers = headersMap.entrySet().stream()
                .map(it -> it.getKey() + "=" + it.getValue())
                .collect(Collectors.joining(","));
        return "headers={" + headers + "}";
    }

    private String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
                .map(qs -> "?" + qs)
                .orElse(Strings.EMPTY);
    }
}
