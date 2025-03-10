package ru.shalkan.loggingstarter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import io.micrometer.common.util.StringUtils;
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
            return doMasking(document);
        } catch (JsonProcessingException e) {
            log.warn("Ошибка при маскировании", e);
            return body.toString();
        }
    }

    public String prepareMaskedBody(String body) {
        Object document = conf.jsonProvider().parse(body);
        return doMasking(document);
    }

    private String doMasking(Object document) {
        if (StringUtils.isEmpty(document.toString())) {
            return "";
        }
        DocumentContext context = JsonPath.parse(document);

        for (String attributePath : logginStarterProperties.getWebRequestBodyMaskedProps()) {
            try {
                context.map(attributePath, (o, configuration) -> "***");
            } catch (PathNotFoundException e) {
                log.warn("Свойство {} для маскирования не найдено в запросе", attributePath);
            }
        }
        return context.jsonString();
    }
}
