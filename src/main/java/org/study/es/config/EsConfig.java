package org.study.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

    @Autowired
    private EsProperties esProperties;

    @Bean
    public RestHighLevelClient initClient() {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(esProperties.getHost(), Integer.valueOf(esProperties.getPort()), esProperties.getSchema()));

        restClientBuilder.setRequestConfigCallback(
                requestConfigBuilder -> {
                    requestConfigBuilder.setConnectTimeout(esProperties.getConnectTimeout());
                    requestConfigBuilder.setSocketTimeout(esProperties.getSocketTimeout());
                    return requestConfigBuilder;
                });

        return new RestHighLevelClient(restClientBuilder);
    }

}
