package com.spring_jwt_server.domain.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@Table(name = "TBL_USER")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 200, nullable = false, name = "name")
    private String userName;
    @Column(unique = true, length = 100, nullable = false, name = "uid")
    private String userId;
    @Column(length = 400, nullable = false, name = "pw")
    private String userPassword;

    @Column(name = "cdtm")
    @CreatedDate
    private LocalDateTime createdDate;

    @Builder
    public User(String userName, String userId, String userPassword) {
        this.userName = userName;
        this.userId = userId;
        this.userPassword = userPassword;
    }
}
