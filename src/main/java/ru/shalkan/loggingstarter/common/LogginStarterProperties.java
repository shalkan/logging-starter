package ru.shalkan.loggingstarter.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("logging")
public class LogginStarterProperties {

    /**
     * json пути, которые будут маскированы при логировании тела запроса, разделенные ;
     * <a href="https://github.com/json-path/JsonPath">lib that used to work with json paths</a>
     */
    private List<String> webRequestBodyMaskedProps;

    public List<String> getWebRequestBodyMaskedProps() {
        return webRequestBodyMaskedProps;
    }

    public void setWebRequestBodyMaskedProps(List<String> webRequestBodyMaskedProps) {
        this.webRequestBodyMaskedProps = webRequestBodyMaskedProps;
    }
}
