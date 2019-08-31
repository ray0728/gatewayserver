package com.rcircle.service.gateway.clients;

import feign.Client;
import feign.Feign;
import feign.RequestInterceptor;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteSsoClientConfiguration {
    @Value("${server.ssl.key-store.path}")
    private String keyStore;
    @Value("${server.ssl.key-store-password}")
    private String password;
    @Bean
    public Feign.Builder feignBuilder() {
        final Client trustSSLSockets = client();
        return Feign.builder().client(trustSSLSockets);
    }

    @Bean
    public Client client() {
        return new Client.Default(
                TrustingSSLSocketFactory.get("authserver", keyStore, password), new NoopHostnameVerifier());
    }

    @Bean
    public RequestInterceptor OAuth2SsoRequestInterceptor() {
        return new RemoteSsoRequestInterceptor();
    }

}
