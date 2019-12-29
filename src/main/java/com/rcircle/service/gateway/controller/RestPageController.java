package com.rcircle.service.gateway.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rcircle.service.gateway.clients.RemoteRequestWithTokenInterceptor;
import com.rcircle.service.gateway.model.*;
import com.rcircle.service.gateway.services.*;
import com.rcircle.service.gateway.utils.Base64;
import com.rcircle.service.gateway.utils.HttpContextHolder;
import com.rcircle.service.gateway.utils.Toolkit;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/rst")
public class RestPageController {
    private static final int AI_TYPE_IP = 0;
    private static final int AI_TYPE_PORT = 1;
    private static final int AI_TYPE_IP_AND_PORT = 2;

    @Resource
    private OAuth2SsoService oAuth2SsoService;
    @Resource
    private MessageService messageService;
    @Resource
    private ResourceService resourceService;
    @Resource
    private AccountService accountService;
    @Resource
    private LocationService locationService;

    @GetMapping("/redirect")
    public String authcode(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state) {
        Map<String, String> result = new HashMap<>();
        result.put("code", code);
        result.put("state", state);
        return JSONObject.toJSONString(result);
    }

    @GetMapping("/ai")
    public String ai(@RequestParam(name = "type") int type) {
        String port = "10006";
        String ip = Toolkit.getLocalIP();
        switch (type) {
            case AI_TYPE_IP:
                return ip;
            case AI_TYPE_PORT:
                return port;
            case AI_TYPE_IP_AND_PORT:
                return ip + ":" + port;
        }
        return "";
    }

    @GetMapping("/hls")
    public String getHlsResult(Principal principal,
                               @RequestParam(name = "lid") int logid,
                               @RequestParam(name = "file") String filename) {
        LogFile log = resourceService.getBlog(logid, true);
        Account opAccount = accountService.getAccountInfo(0, principal.getName());
        if (log == null || log.getUid() != opAccount.getUid()) {
            throw new PermissionDeniedDataAccessException("You don't have permission to access this data", null);
        }
        return "" + messageService.checkHLSResult(logid, filename);
    }

    @DeleteMapping("/hls")
    public String delAllHlsResult(Principal principal, @RequestParam(name = "lid") int logid) {
        LogFile log = resourceService.getBlog(logid, false);
        Account opAccount = accountService.getAccountInfo(0, principal.getName());
        if (log.getUid() == opAccount.getUid()) {
            messageService.clearAllHLSResultFor(logid);
        }
        return "";
    }

    @GetMapping("/lab/voice/device")
    public String uploadLocation(@RequestParam(name = "name") String name,
                                 @RequestParam(name = "info") String info,
                                 @RequestParam(name = "lat") String lat,
                                 @RequestParam(name = "lon") String lon) {
        locationService.updateLocation(name, info, lat, lon);
        return "";
    }

    @GetMapping("/lab/voice")
    public String getLocationBy(@RequestParam(name = "name") String name) {
        LocationDevice device = locationService.getDeviceByName(Base64.decode(name));
        return device == null ? "" : JSONObject.toJSONString(device.getLocation());
    }

    @PostMapping("/account/check")
    public String createNewAccount(HttpServletResponse response,
                                   @RequestParam(name = "username") String name,
                                   @RequestParam(name = "email") String email) {
        String ret = "";
        if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
            response.setStatus(400);
            return "User name and password must be required";
        }
        int errcode = accountService.isExist(name, null) | accountService.isExist(null, email);
        switch (errcode) {
            case AccountService.ERR_USERNAME_EXIST:
                response.setStatus(400);
                ret = "Username has been used";
                break;
            case AccountService.ERR_EMAIL_EXIST:
                response.setStatus(400);
                ret = "Email has been used";
                break;
            case AccountService.ERR_USERNAME_EMAIL_EXIST:
                response.setStatus(400);
                ret = "Username and email are already in use";
                break;
            case AccountService.ERR_SERVER_BUSY:
                response.setStatus(400);
                ret = "Remote service busy, try again later";
                break;
            default:
                break;
        }
        return ret;
    }

    @PostMapping("/account/changepass")
    public String changePassword(HttpServletResponse response,
                                 @RequestParam(name = "old") String oldpass,
                                 @RequestParam(name = "new") String newpass) {
        String ret = "";
        Account account = accountService.changePassword(oldpass, newpass);
        if (account.hasError()) {
            response.setStatus(400);
            ret = account.getErrinfo();
        }
        return ret;
    }

    @PostMapping("/account/changeprofile")
    public String changeProfile(HttpServletResponse response,
                                @RequestParam(name = "email", required = false, defaultValue = "") String email,
                                @RequestParam(name = "signature", required = false, defaultValue = "") String signature,
                                @RequestParam(name = "resume", required = false, defaultValue = "") String resume) {
        String ret = "";
        Account account = accountService.changeProfile(email, signature, resume);
        if (account.hasError()) {
            response.setStatus(400);
            ret = account.getErrinfo();
        }
        return ret;
    }

    @GetMapping("/invitation/check_code")
    public String getCodeId(HttpServletResponse response,
                            @RequestParam(name = "code") String code) {
        if (code == null || code.isEmpty()) {
            response.setStatus(400);
            return "A valid invitation code must be provided";
        }
        int id = accountService.getInvitationCodeId(code);
        if (id == 0) {
            response.setStatus(400);
            return "A valid invitation code must be provided";
        }
        return String.valueOf(id);
    }

    @PostMapping("/invitation/update")
    public String updateInvitationCode(HttpServletResponse response,
                                       @RequestParam(name = "id") int id,
                                       @RequestParam(name = "uid") int uid,
                                       @RequestParam(name = "code") String code) {
        accountService.updateInvitationCode(id, uid, code);
        return "";
    }

    @GetMapping("/author/avatar")
    public ResponseEntity authorAvatar() {
        String errinfo;
        try {
            return createResponseEntity("/mnt/img/me.jpg");
        } catch (IOException e) {
            errinfo = e.getMessage();
        }
        return ResponseEntity.status(404).body(errinfo);
    }

    ResponseEntity createResponseEntity(String filePath) throws IOException {
        MediaType mediaType = MediaType.parseMediaType("image/jpg");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        inputStream.close();
        return new ResponseEntity(bytes, headers, HttpStatus.OK);
    }
}
