package com.spotroute.persistence.token;

import com.spotroute.persistence.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    private boolean revoked=false;
}
