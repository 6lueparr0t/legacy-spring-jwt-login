package com.spring_jwt_server.domain.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@Table(name = "TBL_TOKEN")
@EntityListeners(AuditingEntityListener.class)
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, name = "uid")
    private String userId;

    @Column(length = 400)
    private String access;

    @Convert(converter = UseCodeConverter.class)
    @Column(columnDefinition = "char", name = "use")
    private UseCode useCode;

    @Column(name = "cdtm")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "udtm")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Builder
    public Token(String userId, UseCode useCode) {
        this.userId = userId;
        this.useCode = useCode;
    }

    public void update(String userId, String access, UseCode useCode) {
        this.userId = userId;
        this.access = access;
        this.useCode = useCode;
    }
}
