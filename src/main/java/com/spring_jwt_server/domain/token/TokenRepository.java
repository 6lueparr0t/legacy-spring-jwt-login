package com.spring_jwt_server.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    public Token findByIdAndUserIdAndUseCode(Long id, String userId, UseCode useCode);
}
