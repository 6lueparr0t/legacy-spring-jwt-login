package com.spring_jwt_server.service.sign;

import com.spring_jwt_server.domain.user.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public interface SignService {
    Map<String, Object> signUp(User user, HttpServletRequest request) throws IOException;
    Map<String, String> signOut(String authorizationHeader) throws IOException;
    Map<String, String> signCheck(String authorizationHeader) throws IOException;
}
