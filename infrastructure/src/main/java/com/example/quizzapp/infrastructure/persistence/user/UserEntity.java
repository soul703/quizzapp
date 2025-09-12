package com.example.quizzapp.infrastructure.persistence.user;

// ... imports
import com.example.quizzapp.domain.user.UserRole;
import com.example.quizzapp.domain.user.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity @Table(name = "users") @Getter @Setter
public class UserEntity {
    @Id private UUID id;
    @Column(unique = true, nullable = false) private String email;
    @Column(unique = true, nullable = false) private String firstName;
    @Column(unique = true, nullable = false) private String lastName;
    @Column(name = "password_hash", nullable = false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private UserRole role;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private UserStatus status;
    @Column(name = "password_failed_count", nullable = false) private int passwordFailedCount;
}