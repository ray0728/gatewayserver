package com.rcircle.service.gateway.services;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.rcircle.service.gateway.clients.RemoteSsoClient;
import com.rcircle.service.gateway.clients.RemoteSsoRequestInterceptor;
import com.rcircle.service.gateway.utils.HttpContextHolder;
import com.rcircle.service.gateway.utils.Toolkit;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OAuth2SsoService {

    @Value("${security.oauth2.client.client-id}")
    private String clientid;
    @Value("${security.oauth2.client.client-secret}")
    private String secret;
    @Value("${server.port}")
    private int port;
    @Value("${trust.config.id}")
    private String trustConfigId;
    @Value("${trust.config.secret}")
    private String trustConfigSecret;

    @Autowired
    private RemoteSsoClient remoteSsoClient;

    @HystrixCommand(fallbackMethod = "buildFallbackGetTrustToken", threadPoolKey = "AccessTokenThreadPool")
    public String getTrustToken(){
        return remoteSsoClient.getTrustToken(trustConfigId, trustConfigSecret);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackGetCode", threadPoolKey = "AccessTokenThreadPool")
    public String getAuthorizeCode(Map parameters, String state){
        parameters.put("response_type", "code");
        parameters.put("client_id", clientid);
        parameters.put("redirect_uri", extractRedirect("/rst/redirect"));
        parameters.put("state", state);
        return remoteSsoClient.getAuthorizeCode(parameters);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackGetToken", threadPoolKey = "AccessTokenThreadPool")
    public String getToken(Map<String, String>parameters, String code){
        parameters.remove("response");
        parameters.put("client_secret", secret);
        parameters.put("grant_type", "authorization_code");
        parameters.put("code", code);
        return remoteSsoClient.getAccessToken(parameters);
    }

    public String buildFallbackGetCode(Map parameter, String obj, Throwable throwable) {
        return autoDetectException(throwable);
    }

    public String buildFallbackGetToken(Map<String, String> parameter, String obj, Throwable throwable) {
     return autoDetectException(throwable);
    }

    public String buildFallbackGetTrustToken(Throwable throwable){
        return autoDetectException(throwable);
    }

    private String autoDetectException(Throwable throwable){
        int status = 0;
        String reason = null;
        if (throwable instanceof FeignException) {
            status = ((FeignException) throwable).status();
        }
        switch (status) {
            case 401:
                reason = "Incorrect Password!";
                break;
            case 500:
                reason = "Server Busy";
                break;
            default:
                reason ="Network Timeout";
                break;
        }
        return "failed! " + reason;
    }

    private String extractRedirect(String endpoint) {
        return String.format("http://%s:%d%s", Toolkit.getLocalIP(), port, endpoint);
    }
}
