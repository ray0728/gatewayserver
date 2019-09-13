package com.rcircle.service.gateway.clients;

import feign.Client;
import feign.Feign;
import feign.RequestInterceptor;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteSsoClientConfiguration {
    @Value("${server.ssl.key-store}")
    private String keyStore;
    @Value("${server.ssl.key-store-password}")
    private String password;

//    @Bean
//    public Feign.Builder feignBuilder() {
//        Client trustSSLSockets = new Client.Default(
//                TrustingSSLSocketFactory.get("", keyStore, password), new NoopHostnameVerifier());
//        return Feign.builder().client(trustSSLSockets);
//    }

    @Bean
    public RequestInterceptor OAuth2SsoRequestInterceptor() {
        return new RemoteSsoRequestInterceptor();
    }

}
