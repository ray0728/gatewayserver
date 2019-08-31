package com.rcircle.service.gateway.clients;

import feign.Client;
import feign.Feign;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpsConfiguration {
    @Value("${server.ssl.key-store}")
    private String keyStore;
    @Bean
    public Feign.Builder feignBuilder() {
        final Client trustSSLSockets = client();
        return Feign.builder().client(trustSSLSockets);
    }

    @Bean
    public Client client() {
        return new Client.Default(
                TrustingSSLSocketFactory.get(keyStore), new NoopHostnameVerifier());
    }
}
