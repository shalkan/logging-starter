package ru.shalkan.loggingstarter.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("logging")
public class LoggingStarterProperties {

    /**
     * json пути, которые будут маскированы при логировании тела запроса, разделенные ;
     * <a href="https://github.com/json-path/JsonPath">lib that used to work with json paths</a>
     */
    private List<String> webRequestBodyMaskedProps;

    private Boolean logFeignRequestsBody = true;

    public List<String> getWebRequestBodyMaskedProps() {
        return webRequestBodyMaskedProps;
    }

    public void setWebRequestBodyMaskedProps(List<String> webRequestBodyMaskedProps) {
        this.webRequestBodyMaskedProps = webRequestBodyMaskedProps;
    }

    public Boolean getLogFeignRequestsBody() {
        return logFeignRequestsBody;
    }

    public void setLogFeignRequestsBody(Boolean logFeignRequestsBody) {
        this.logFeignRequestsBody = logFeignRequestsBody;
    }
}
