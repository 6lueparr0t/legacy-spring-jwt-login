package com.spring_jwt_server.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.spring_jwt_server.common.library.DateUtils;
import com.spring_jwt_server.domain.Login;
import com.spring_jwt_server.domain.token.Token;
import static com.spring_jwt_server.domain.token.UseCode.NULL;
import static com.spring_jwt_server.domain.token.UseCode.USE;
import com.spring_jwt_server.service.token.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final String APPLICATION_JSON_VALUE = "application/json";
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, ApplicationContext ctx) {
        this.authenticationManager = authenticationManager;
        this.tokenService = ctx.getBean(TokenService.class);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        Login login = this.getLoginRequest(request);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getUserId(), login.getUserPassword());
        return authenticationManager.authenticate(authenticationToken);
    }

    private Login getLoginRequest(HttpServletRequest request) {
        BufferedReader reader = null;
        Login login = null;
        try {
            reader = request.getReader();
            Gson gson = new Gson();
            login = gson.fromJson(reader, Login.class);
        } catch (IOException e) {
            log.debug("{}", e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.debug("{}", e);
            }
        }

        return login;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        Token createdToken = tokenService.saveToken(new Token(user.getUsername(), NULL));

        Date date = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
        String access_token = tokenService.generateToken(createdToken.getId(), user.getUsername(), request.getRequestURI(), date);

        createdToken.setAccess(access_token);
        createdToken.setUseCode(USE);
        tokenService.updateToken(createdToken.getId(), createdToken);

        Map<String,String> token = new HashMap<>();
        token.put("access", access_token);
        token.put("expired", DateUtils.getDateString(date));

        Map<String, Object> data = new HashMap<>();
        data.put("status", "success");
        data.put("token", token);

        response.setContentType(APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(), data);
    }
}
