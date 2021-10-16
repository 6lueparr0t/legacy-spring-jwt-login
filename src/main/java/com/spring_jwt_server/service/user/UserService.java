package com.spring_jwt_server.service.user;

import com.spring_jwt_server.domain.user.User;

public interface UserService {
    public User saveUser(User user);
    public User getUser(String userId);
    public User checkUser(String userId, String userPassword);
}
