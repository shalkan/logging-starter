package ru.shalkan.loggingstarter.feign;

import feign.Logger;
import feign.Request;
import feign.Response;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import ru.shalkan.loggingstarter.common.LoggingStarterProperties;
import ru.shalkan.loggingstarter.dto.RequestDirection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeignRequestLogger extends Logger {

    @Autowired
    private LoggingStarterProperties loggingStarterProperties;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FeignRequestLogger.class);

    @Override
    protected void log(String s, String s1, Object... objects) {
        // реалзация не требуется
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        String method = request.httpMethod().name();
        String requestURI = request.url();
        String headers = inlineHeaders(request);


        if (loggingStarterProperties.getLogFeignRequestsBody()) {
            String body = new String(request.body(), StandardCharsets.UTF_8);
            log.info("[{}] Запрос: {} {} {} body={}", RequestDirection.OUT, method, requestURI, headers, body);
        } else {
            log.info("[{}] Запрос: {} {} {}", RequestDirection.OUT, method, requestURI, headers);
        }

    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        String  responseBody = StreamUtils.copyToString(response.body().asInputStream(),  StandardCharsets.UTF_8);

        String method = response.request().httpMethod().name();
        String url = response.request().url();
        int status = response.status();

        if (loggingStarterProperties.getLogFeignRequestsBody()) {
            log.info("[{}] Ответ: {} {} {} body={}", RequestDirection.OUT, method, url, status, responseBody);
        } else {
            log.info("[{}] Ответ: {} {} {}", RequestDirection.OUT, method, url, status);
        }

        return response.toBuilder()
                .body(responseBody, StandardCharsets.UTF_8)
                .build();
    }

    private String inlineHeaders(Request request) {
        Map<String, Collection<String>> headersMap = request.requestTemplate().headers();
        String headers = headersMap.entrySet().stream()
                .map(it -> it.getKey() + "=" + it.getValue())
                .collect(Collectors.joining(","));
        return "headers={" + headers + "}";
    }
}
