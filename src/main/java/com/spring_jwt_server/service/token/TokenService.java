package com.spring_jwt_server.service.token;

import com.auth0.jwt.interfaces.Claim;
import com.spring_jwt_server.domain.token.Token;
import com.spring_jwt_server.domain.token.UseCode;

import java.util.Date;
import java.util.Map;

public interface TokenService {
    Token saveToken(Token token);
    Long updateToken(Long id, Token token);
    Token getToken(Long id, String userId, UseCode useCode);
    Map<String, Claim> checkToken(String authorizationHeader);
    String generateToken(Long id, String userId, String requestURI, Date date);
    String getUserIdInToken(String authorizationHeader);
    String getSecret();
}
