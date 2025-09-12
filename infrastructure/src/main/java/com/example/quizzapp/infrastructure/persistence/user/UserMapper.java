package com.example.quizzapp.infrastructure.persistence.user;
// ... imports
import com.example.quizzapp.domain.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        return User.hydrate(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getStatus(),
                entity.getPasswordFailedCount()
        );
    }

    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setRole(domain.getRole());
        entity.setStatus(domain.getStatus());
        entity.setPasswordFailedCount(domain.getPasswordFailedCount());
        return entity;
    }
}