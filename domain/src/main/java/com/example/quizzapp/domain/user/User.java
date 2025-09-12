package com.example.quizzapp.domain.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Dùng cho mapper
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Dùng cho JPA/framework
public class User {
    // Không dùng AbstractAggregateRoot ở đây để giữ domain hoàn toàn độc lập
    // Việc publish event sẽ do Repository đảm nhiệm

    private static final int MAX_LOGIN_FAILURE_COUNT = 5;

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private UserRole role;
    private UserStatus status;
    private int passwordFailedCount;

    /**
     * Factory method để tái tạo lại một User object từ dữ liệu đã tồn tại.
     * Dành riêng cho tầng persistence.
     * "Hydrate" có nghĩa là "làm sống lại" một object từ dữ liệu khô.
     */
    public static User hydrate(
            UUID id,
            String email,
            String firstName,
            String lastName,
            String passwordHash,
            UserRole role,
            UserStatus status,
            int passwordFailedCount
    ) {
        // Có thể thêm các Assert.notNull ở đây nếu cần để đảm bảo dữ liệu từ DB là hợp lệ
        return new User(id, email, firstName,lastName,passwordHash, role, status, passwordFailedCount);
    }
    public static User register(String email, String passwordHash, UserRole role) {
        Assert.hasText(email, "Email must not be empty");
        Assert.hasText(passwordHash, "Password hash must not be empty");
        Assert.notNull(role, "Role must not be null");

        User user = new User();
        user.id = UUID.randomUUID();
        user.email = email.toLowerCase().trim();
        user.passwordHash = passwordHash;
        user.role = role;
        user.status = UserStatus.ACTIVE;
        user.passwordFailedCount = 0;
        return user;
    }

    // --- Business Methods ---
    public void handleSuccessfulLogin() {
        this.passwordFailedCount = 0;
    }

    public void handleFailedLoginAttempt() {
        if (this.status != UserStatus.ACTIVE) return;
        this.passwordFailedCount++;
        if (this.passwordFailedCount >= MAX_LOGIN_FAILURE_COUNT) {
            this.status = UserStatus.LOCKED;
        }
    }
}