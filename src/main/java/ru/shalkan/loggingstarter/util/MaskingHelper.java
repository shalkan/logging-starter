package ru.shalkan.loggingstarter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shalkan.loggingstarter.common.LogginStarterProperties;

public class MaskingHelper {

    private static final Logger log = LoggerFactory.getLogger(MaskingHelper.class);

    @Autowired
    private LogginStarterProperties logginStarterProperties;

    @Autowired
    private ObjectMapper mapper;
    private final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.REQUIRE_PROPERTIES);

    public String prepareMaskedBody(Object body) {
        try {
            Object document = conf.jsonProvider().parse(mapper.writeValueAsString(body));
            DocumentContext context = JsonPath.parse(document);

            for (String attributePath : logginStarterProperties.getWebRequestBodyMaskedProps()) {
                try {
                    Object val = JsonPath.read(document, attributePath);
                    if (val != null) {
                        context = context.set(attributePath, "***");
                    }
                } catch (PathNotFoundException e) {
                    log.warn("Свойство {} для маскирования не найдено в запросе", attributePath);
                }
            }
            return context.jsonString();
        } catch (JsonProcessingException e) {
            log.warn("Ошибка при маскировании", e);
            return body.toString();
        }
    }

    public String prepareMaskedBody(String body) {
        Object document = conf.jsonProvider().parse(body);
        DocumentContext context = JsonPath.parse(document);

        for (String attributePath : logginStarterProperties.getWebRequestBodyMaskedProps()) {
            try {
                Object val = JsonPath.read(document, attributePath);
                if (val != null) {
                    context = context.set(attributePath, "***");
                }
            } catch (PathNotFoundException e) {
                log.warn("Свойство {} для маскирования не найдено в запросе", attributePath);
            }
        }
        return context.jsonString();
    }
}
