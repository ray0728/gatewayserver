package com.rcircle.service.gateway.clients;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@FeignClient(name = "service-account", configuration = RemoteRequestWithTokenConfiguration.class)
public interface RemoteAccountClient {
    @RequestMapping(method = RequestMethod.GET, value = "/account/info")
    public String getInfo(@RequestParam(name = "username") String username, @RequestParam(name = "uid") int id);

    @RequestMapping(method = RequestMethod.PUT, value = "/account/refresh")
    public String refreshTime();

    @RequestMapping(method = RequestMethod.PUT, value = "/account/password")
    public String changePassword(@RequestParam(name = "old") String oldPass, @RequestParam(name = "new") String newPass);

    @RequestMapping(method = RequestMethod.PUT, value = "/account/edit")
    public String changeProifle(@RequestParam(name = "email") String email,
                                @RequestParam(name = "signature") String signature,
                                @RequestParam(name = "resume") String resume);


    @RequestMapping(method = RequestMethod.GET, value = "/invitation/check_code")
    public int checkCode(@RequestParam(name = "code") String code);

    @RequestMapping(method = RequestMethod.GET, value = "/invitation/update")
    public String updateCode(@RequestParam(name = "uid") int uid,
                             @RequestParam(name = "cid") int cid,
                             @RequestParam(name = "code") String code);
}
