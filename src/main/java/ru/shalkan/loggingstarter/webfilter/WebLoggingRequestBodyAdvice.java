package ru.shalkan.loggingstarter.webfilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import ru.shalkan.loggingstarter.common.ConfigProperties;

import java.lang.reflect.Type;
import java.util.Optional;

@ControllerAdvice
public class WebLoggingRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingRequestBodyAdvice.class);

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ConfigProperties configProperties;

    @Autowired
    private ObjectMapper mapper;


    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String bodyString = body.toString();
        try {
            String maskedAttribetesProperty = configProperties.getWebRequestBodyMaskedProps();
            String[] attributesForMasking = maskedAttribetesProperty.split(";");
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(mapper.writeValueAsString(body));
            DocumentContext context = JsonPath.parse(document);

            for (String attributePath : attributesForMasking) {
                try {
                    Object val = JsonPath.read(document, attributePath);
                    if (val != null) {
                        context = context.set(attributePath, "***");
                    }
                } catch (PathNotFoundException e) {
                    log.warn("Свойство {} для маскирования не найдено в запросе", attributePath);
                }
                bodyString = context.jsonString();
            }
        } catch (JsonProcessingException e) {
            log.warn("Ошибка при маскировании", e);
        }

        String method = httpServletRequest.getMethod();
        String requestURI = httpServletRequest.getRequestURI() + formatQueryString(httpServletRequest);

        log.info("Тело запроса: {} {} {}", method, requestURI, bodyString);

        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    private String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
                .map(qs -> "?" + qs)
                .orElse(Strings.EMPTY);
    }
}
