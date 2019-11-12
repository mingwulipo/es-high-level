package org.study.es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class EsProperties {

    private String host;

    private String port;

    private String schema;

    private Integer connectTimeout;

    private Integer socketTimeout;

    private String account;

    private String password;

}
