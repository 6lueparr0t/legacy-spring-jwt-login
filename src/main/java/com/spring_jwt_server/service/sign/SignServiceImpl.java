package com.spring_jwt_server.service.sign;

import com.auth0.jwt.interfaces.Claim;
import com.spring_jwt_server.common.library.DateUtils;
import com.spring_jwt_server.domain.token.Token;
import com.spring_jwt_server.domain.user.User;
import com.spring_jwt_server.service.token.TokenService;
import com.spring_jwt_server.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.spring_jwt_server.domain.token.UseCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignServiceImpl implements SignService {
    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public Map<String, Object> signUp(User user, HttpServletRequest request) throws IOException {
        try {
            User createdUser = userService.saveUser(user);
            Map<String, Object> data = new HashMap<>();
            if (createdUser != null) {
                data.put("status", "success");
                Token createdToken = tokenService.saveToken(new Token(user.getUserId(), NULL));

                Date date = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
                String access_token = tokenService.generateToken(createdToken.getId(), user.getUserId(), request.getRequestURI(), date);

                createdToken.setAccess(access_token);
                createdToken.setUseCode(USE);
                tokenService.updateToken(createdToken.getId(), createdToken);

                Map<String,String> token = new HashMap<>();
                token.put("access", access_token);
                token.put("expired", DateUtils.getDateString(date));

                data.put("status", "success");
                data.put("token", token);
            } else {
                data.put("status", "fail");
            }

            return data;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IOException(e);
        }
    }
    @Override
    public Map<String, String> signOut(String authorizationHeader) {
        Map<String, String> data = new HashMap<>();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                Map<String, Claim> claimMap = tokenService.checkToken(authorizationHeader);

                Token token = tokenService.getToken(claimMap.get("Id").asLong(), claimMap.get("UserId").asString(), USE);
                token.setUseCode(NOT);
                tokenService.updateToken(token.getId(), token);

                data.put("status", "success");
                data.put("msg", "sign out success!");
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                data.put("status", "fail");
                data.put("msg", "error occured!");
            }

            return data;
        } else {
            throw new RuntimeException("It's not a valid token.");
        }
    }

    @Override
    public Map<String, String> signCheck(String authorizationHeader) throws IOException {
        Map<String, String> data = new HashMap<>();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                Map<String, Claim> claimMap = tokenService.checkToken(authorizationHeader);

                Token token = tokenService.getToken(claimMap.get("Id").asLong(), claimMap.get("UserId").asString(), USE);

                if (token == null) {
                    data.put("status", "fail");
                    data.put("msg", "token has not found.");
                } else {
                    data.put("status", "success");
                    data.put("msg", "valid token.");
                }
            } catch (Exception e) {
                log.error("Error logging in: {}", e.getMessage());
                data.put("status", "fail");
                data.put("msg", "error occured!");
            }

            return data;
        } else {
            throw new RuntimeException("It's not a valid token.");
        }
    }
}
