package ru.shalkan.loggingstarter.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("logging")
public class ConfigProperties {

    /**
     * json пути, которые будут маскированы при логировании тела запроса, разделенные ;
     * <a href="https://github.com/json-path/JsonPath">lib that used to work with json paths</a>
     */
    private String webRequestBodyMaskedProps;

    public String getWebRequestBodyMaskedProps() {
        return webRequestBodyMaskedProps;
    }

    public void setWebRequestBodyMaskedProps(String webRequestBodyMaskedProps) {
        this.webRequestBodyMaskedProps = webRequestBodyMaskedProps;
    }
}
