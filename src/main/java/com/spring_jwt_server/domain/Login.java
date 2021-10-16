package com.spring_jwt_server.domain;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class Login {
    @NotNull
    private String userId;
    @NotNull
    private String userPassword;

    @Builder
    public Login(String userId, String userPassword) {
        this.userId = userId;
        this.userPassword = userPassword;
    }
}