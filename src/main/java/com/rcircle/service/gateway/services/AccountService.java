package com.rcircle.service.gateway.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.rcircle.service.gateway.clients.RemoteAccountClient;
import com.rcircle.service.gateway.model.Account;
import com.rcircle.service.gateway.model.LogFile;
import com.rcircle.service.gateway.model.ResultData;
import com.rcircle.service.gateway.utils.Toolkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {
    public static final int ERR_USERNAME_EXIST = 1;
    public static final int ERR_EMAIL_EXIST = 2;
    public static final int ERR_USERNAME_EMAIL_EXIST = 3;
    public static final int ERR_SERVER_BUSY = 7;
    @Autowired
    private RemoteAccountClient remoteAccountClient;

    @HystrixCommand(fallbackMethod = "buildFallbackChangeProfile", threadPoolKey = "AccountThreadPool")
    public Account changeProfile(String email, String signature, String resume) {
        String info = remoteAccountClient.changeProifle(email, signature, resume);
        ResultData data = JSON.parseObject(info, ResultData.class);
        return JSON.parseObject(data.getMap().get("account").toString(), Account.class);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackChangePassword", threadPoolKey = "AccountThreadPool")
    public Account changePassword(String oldpass, String newpass) {
        String info = remoteAccountClient.changePassword(oldpass, newpass);
        ResultData data = JSON.parseObject(info, ResultData.class);
        return JSON.parseObject(data.getMap().get("account").toString(), Account.class);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackGetAccountInfo", threadPoolKey = "AccountThreadPool")
    public Account getAccountInfo(int id, String name) {
        String info = remoteAccountClient.getInfo(name, id);
        return JSON.parseObject(info, Account.class);
    }

    public List<Account> getAllAccountBasicInfo() {
        String info = remoteAccountClient.getInfo("", 0);
        return JSON.parseArray(info, Account.class);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackGetAllAcounts", threadPoolKey = "AccountThreadPool")
    public List<Account> getAllAccounts() {
        String ret = remoteAccountClient.getInfo("", 0);
        return JSONArray.parseArray(ret, Account.class);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackisExist", threadPoolKey = "AccountThreadPool")
    public int isExist(String username, String email) {
        Account existAccount = null;
        if(username != null && !username.isEmpty()){
            existAccount = getAccountInfo(0, username);
            if (existAccount != null && !existAccount.hasError()) {
                return ERR_USERNAME_EXIST;
            }
        }else if(email != null && !email.isEmpty()){
            existAccount = getAccountInfo(0, email);
            if (existAccount != null && !existAccount.hasError()) {
                return ERR_EMAIL_EXIST;
            }
        }else{
            return ERR_SERVER_BUSY;
        }
        return 0;
    }

    @HystrixCommand(fallbackMethod = "buildFallbackGetInviationCodeId", threadPoolKey = "AccountThreadPool")
    public int getInvitationCodeId(String code){
        return remoteAccountClient.checkCode(code);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackUpdateInviationCode", threadPoolKey = "AccountThreadPool")
    public String updateInvitationCode(int id, int uid, String code){
        return remoteAccountClient.updateCode(uid, id, code);
    }

    @HystrixCommand(fallbackMethod = "buildFallbackAfterLoginSuccess", threadPoolKey = "AccountThreadPool")
    public Account afterLoginSuccess() {
        String ret = remoteAccountClient.refreshTime();
        ResultData data = JSON.parseObject(ret, ResultData.class);
        return JSON.parseObject(data.getMap().get("account").toString(), Account.class);
    }

    private String autoDetectErrinfo(Throwable throwable) {
        String failinfo = "failed!";
        if (throwable instanceof com.netflix.hystrix.exception.HystrixTimeoutException) {
            failinfo += "remote service is busy now, please retry it late";
        } else {
            failinfo += throwable.getMessage();
        }
        return failinfo;
    }

    private Account createErrorAccount(Throwable throwable){
        Account account = new Account();
        account.setUsername("anonymous");
        account.setUid(0);
        account.setSignature("隠された神");
        account.setErrinfo(autoDetectErrinfo(throwable));
        return account;
    }

    private Account buildFallbackChangeProfile(String email, String signature, String resume, Throwable throwable){
        return createErrorAccount(throwable);
    }

    private Account buildFallbackChangePassword(String newpass, String oldpass, Throwable throwable){
        return createErrorAccount(throwable);
    }

    private List<Account> buildFallbackGetAllAcounts(Throwable throwable) {
        return null;
    }

    private Account buildFallbackAfterLoginSuccess(Throwable throwable) {
        return createErrorAccount(throwable);
    }

    private Account buildFallbackGetAccountInfo(int id, String name, Throwable throwable) {
        return createErrorAccount(throwable);
    }

    private int buildFallbackisExist(String username, String email, Throwable throwable) {
        return ERR_SERVER_BUSY;
    }

    private int buildFallbackGetInviationCodeId(String code, Throwable throwable){
        return 0;
    }

    private String buildFallbackUpdateInviationCode(int id, int uid, String code, Throwable throwable){
        return autoDetectErrinfo(throwable);
    }
}
