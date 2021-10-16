package com.spring_jwt_server.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    public User findByUserId(String userId);
    public User findByUserIdAndUserPassword(String userId, String userPassword);
}
