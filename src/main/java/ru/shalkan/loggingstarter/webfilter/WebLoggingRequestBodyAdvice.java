package ru.shalkan.loggingstarter.webfilter;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.shalkan.loggingstarter.common.LogginStarterProperties;
import ru.shalkan.loggingstarter.util.MaskingHelper;

import java.lang.reflect.Type;
import java.util.Optional;

@ControllerAdvice
public class WebLoggingRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingRequestBodyAdvice.class);

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private LogginStarterProperties logginStarterProperties;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MaskingHelper maskingHelper;


    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        String method = httpServletRequest.getMethod();
        String requestURI = httpServletRequest.getRequestURI() + formatQueryString(httpServletRequest);

        log.info("Тело запроса: {} {} {}", method, requestURI, maskingHelper.prepareMaskedBody(body));

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
