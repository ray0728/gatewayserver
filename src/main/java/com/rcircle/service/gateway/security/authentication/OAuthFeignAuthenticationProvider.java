package com.rcircle.service.gateway.security.authentication;

import com.alibaba.fastjson.JSON;
import com.rcircle.service.gateway.clients.RemoteRequestWithTokenInterceptor;
import com.rcircle.service.gateway.clients.RemoteSsoRequestInterceptor;
import com.rcircle.service.gateway.model.Account;
import com.rcircle.service.gateway.services.AccountService;
import com.rcircle.service.gateway.services.OAuth2SsoService;
import com.rcircle.service.gateway.utils.HttpContextHolder;
import com.rcircle.service.gateway.utils.Toolkit;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class OAuthFeignAuthenticationProvider implements AuthenticationProvider {
    @Resource
    private OAuth2SsoService oAuth2SsoService;

    @Resource
    private AccountService accountService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = getAccessToken(authentication.getName(), (String) authentication.getCredentials());
        if(token.startsWith("failed!")){
           throw new BadCredentialsException(token);
        }
        ((Account)authentication).setToken(token);
        HttpContextHolder.getContext().setValue(
                RemoteRequestWithTokenInterceptor.ACCESSTOKEN,
                ((Account) authentication).getToken().getAccess_token());
        ((Account)authentication).copyFrom(accountService.afterLoginSuccess());
        authentication.setAuthenticated(true);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Account.class.equals(aClass);
    }

    private String getAccessToken(String username, String password) {
        String info = oAuth2SsoService.getTrustToken();
        if(info.startsWith("failed")){
            return info;
        }
        String[] emt = info.split(":");
        HttpContextHolder.getContext().setValue(emt[0], emt[1]);
        String state = Toolkit.randomString(8);
        HttpContextHolder.getContext().setValue(RemoteSsoRequestInterceptor.USERNAMEANDPASSWORD, username + ":" + password);
        Map<String, String> parameters = new HashMap<>();
        String map = oAuth2SsoService.getAuthorizeCode(parameters, state);
        if(map.startsWith("failed")){
            return map;
        }
        HashMap<String, String> authcode = JSON.parseObject(map, HashMap.class);
        if (!state.equals(authcode.get("state"))) {
            return "failed! state mismatch";
        }
        HttpContextHolder.getContext().delete(RemoteSsoRequestInterceptor.USERNAMEANDPASSWORD);
        return oAuth2SsoService.getToken(parameters, authcode.get("code"));
    }
}
