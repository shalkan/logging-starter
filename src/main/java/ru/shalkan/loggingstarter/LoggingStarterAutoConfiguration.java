package ru.shalkan.loggingstarter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import ru.shalkan.loggingstarter.aspect.LogExecutionAspect;
import ru.shalkan.loggingstarter.common.ConfigProperties;
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
    @ConditionalOnProperty(prefix = "logging", name = "web-request.log-enabled", havingValue = "true")
    public WebLoggingFilter webLoggingFilter() {
        return new WebLoggingFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging", name = "web-request.body.log-enabled", havingValue = "true")
    public WebLoggingRequestBodyAdvice webLoggingRequestBodyAdvice() {
        return new WebLoggingRequestBodyAdvice();
    }

    @Bean
    public ConfigProperties configProperties() {
        return new ConfigProperties();
    }
}
