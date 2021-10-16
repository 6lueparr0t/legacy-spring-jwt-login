package com.spring_jwt_server.domain.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UseCode implements BaseEnumCode<String> {
    USE("Y"),
    NOT("N"),
    NULL(""),
    ;

    private final String value;
}
