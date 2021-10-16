package com.spring_jwt_server.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spring_jwt_server.domain.token.Token;
import com.spring_jwt_server.domain.token.TokenRepository;
import com.spring_jwt_server.domain.token.UseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${spring.jwt.secret}")
    private String jwtSecretKey;

    private final TokenRepository tokenRepository;

    @Override
    public Token saveToken(Token token) {
        return tokenRepository.save(token);
    }

    @Override
    public Long updateToken(Long id, Token token) {
        Token updateToken = tokenRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Token has not found : {}"+ id));

        updateToken.update(token.getUserId(), token.getAccess(), token.getUseCode());

        return id;
    }

    @Override
    public Token getToken(Long id, String userId, UseCode useCode) {
        return tokenRepository.findByIdAndUserIdAndUseCode(id, userId, useCode);
    }

    @Override
    public String generateToken(Long id, String userId, String requestURI, Date date) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecretKey.getBytes());

        return JWT.create()
                .withSubject(userId)
                .withExpiresAt(date)
                .withIssuer(requestURI)
                .withClaim("Id", id)
                .withClaim("UserId", userId)
                .sign(algorithm);
    }

    @Override
    public String getUserIdInToken(String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(jwtSecretKey.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT.getClaim("UserId").toString();
    }

    @Override
    public Map<String, Claim> checkToken(String authorizationHeader) {
        String access_token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(jwtSecretKey.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(access_token);

        Map<String, Claim> claimMap = new HashMap<>();

        claimMap.put("UserId", decodedJWT.getClaim("UserId"));
        claimMap.put("Id", decodedJWT.getClaim("Id"));

        return claimMap;
    }

    @Override
    public String getSecret() {
        return this.jwtSecretKey;
    }
}
