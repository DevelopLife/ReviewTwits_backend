package com.developlife.reviewtwits.controller;

import com.developlife.reviewtwits.config.security.JwtTokenProvider;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.message.request.KakaoOauthReqeust;
import com.developlife.reviewtwits.message.response.KakaoOauthResponse;
import com.developlife.reviewtwits.message.request.LoginUserRequest;
import com.developlife.reviewtwits.message.request.RegisterUserRequest;
import com.developlife.reviewtwits.service.UserService;
import com.developlife.reviewtwits.type.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public String login(@RequestBody LoginUserRequest loginUserRequest) {
        User user = userService.login(loginUserRequest);
        return jwtTokenProvider.createToken(user.getAccountId(), user.getRoles());
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public User register(@RequestBody RegisterUserRequest registerUserRequest) {
        return userService.register(registerUserRequest, Set.of(UserRole.USER));
    }

    @GetMapping(value = "/me", produces = "application/json")
    public User me() {
        // SecurityContext에서 인증받은 회원의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        return userService.getUser(id);
    }

    // admin권한 부여를 받을수 있는 테스트용 메소드
    @PostMapping(value = "/permission", produces = "application/json")
    public User addAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accountId = authentication.getName();
        return userService.grantedAdminPermission(accountId);
    }

    @DeleteMapping(value = "/permission", produces = "application/json")
    public User deleteAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String accountId = authentication.getName();
        return userService.confiscatedAdminPermission(accountId);
    }

    @GetMapping(value = "/admin", produces = "application/json")
    public String admin() {
        return "hello admin";
    }

    @PostMapping(value = "/kakao-oauth", produces = "application/json")
    public KakaoOauthResponse kakaoOauth(@RequestBody KakaoOauthReqeust request) throws IOException, URISyntaxException, InterruptedException {
        return requestKakaoJwtToken(request);
    }

    private KakaoOauthResponse requestKakaoJwtToken(KakaoOauthReqeust reqeust) throws IOException, URISyntaxException, InterruptedException {
        URI uri = new URI("https://kauth.kakao.com/oauth/token");
        System.out.println(reqeust.code());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("code", reqeust.code());
        parameters.put("grant_type", "authorization_code");
        parameters.put("client_id", "3b6b892b63e2f4847f755307fd076b7b");
        parameters.put("redirect_uri", "http://localhost:3000/auth/kakao");


        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://kauth.kakao.com/oauth/token"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();



        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() != 200)
            return KakaoOauthResponse.builder().build();
 
        String content = response.body().toString();
        ObjectMapper objectMapper = new ObjectMapper();
        Object objValue = objectMapper.readValue(content, Object.class);
        System.out.println(response.statusCode());
        return objectMapper.convertValue(objValue, KakaoOauthResponse.class);
    }
}
