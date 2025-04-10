package ru.shalkan.loggingstarter;

import feign.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import ru.shalkan.loggingstarter.aspect.LogExecutionAspect;
import ru.shalkan.loggingstarter.common.LoggingStarterProperties;
import ru.shalkan.loggingstarter.feign.FeignRequestLogger;
import ru.shalkan.loggingstarter.util.MaskingHelper;
import ru.shalkan.loggingstarter.webfilter.WebLoggingFilter;
import ru.shalkan.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;

@AutoConfiguration
public class LoggingStarterAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "logging", name = "execution-log-enabled", havingValue = "true")
    public LogExecutionAspect  logExecutionAspect() {
        return new LogExecutionAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging", name = "web-request.enabled", havingValue = "true")
    public WebLoggingFilter webLoggingFilter() {
        return new WebLoggingFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging", name = "web-request.body.enabled", havingValue = "true")
    public WebLoggingRequestBodyAdvice webLoggingRequestBodyAdvice() {
        return new WebLoggingRequestBodyAdvice();
    }

    @Bean
    public LoggingStarterProperties loggingStarterProperties() {
        return new LoggingStarterProperties();
    }

    @Bean
    public MaskingHelper maskingHelper() {
        return new MaskingHelper();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging", name = "log-feign-requests", havingValue = "true")
    public FeignRequestLogger feignRequestLogger() {
        return new FeignRequestLogger();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging", name = "log-feign-requests", havingValue = "true")
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
