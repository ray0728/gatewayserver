package com.rcircle.service.gateway.clients;

import com.rcircle.service.gateway.utils.HttpContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Map;

public class RemoteRequestWithTokenInterceptor implements RequestInterceptor {
    private static final String BEARER = "Bearer";
    private static final String AUTHORIZATION = "Authorization";
    public static final String ACCESSTOKEN = "accesstoken";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, Object> map = HttpContextHolder.getContext().getMap();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            switch (entry.getKey()) {
                case ACCESSTOKEN:
                    requestTemplate.header(AUTHORIZATION, new String[]{extractToken((String) entry.getValue())});
                    break;
                default:
                    requestTemplate.header(entry.getKey(), (String) entry.getValue());
                    break;
            }
        }
    }

    protected String extractToken(String token) {
        return String.format("%s %s", BEARER, token);
    }
}
