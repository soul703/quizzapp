package com.example.quizzapp.infrastructure.persistence.user;

import com.example.quizzapp.domain.user.User;
import com.example.quizzapp.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final UserMapper userMapper;

    /**
     * Lưu trạng thái của User aggregate vào database.
     * 1. Chuyển đổi User (domain model) thành UserEntity (persistence model).
     * 2. Sử dụng JpaRepository để lưu entity xuống DB.
     * 3. Chuyển đổi UserEntity đã được lưu trở lại thành User domain model để trả về.
     *    (Việc trả về đối tượng đã lưu rất hữu ích vì nó có thể chứa các giá trị được cập nhật bởi DB, ví dụ: timestamp).
     */
    @Override
    public User save(User user) {
        // Chuyển đổi từ Domain sang Entity
        UserEntity entityToSave = userMapper.toEntity(user);

        // Gọi phương thức save của Spring Data JPA
        UserEntity savedEntity = jpaRepository.save(entityToSave);

        // Chuyển đổi kết quả trả về từ Entity sang Domain
        return userMapper.toDomain(savedEntity);
    }

    /**
     * Tìm kiếm một User aggregate bằng email.
     * 1. Sử dụng JpaRepository để tìm UserEntity trong DB.
     * 2. Nếu tìm thấy, kết quả Optional<UserEntity> sẽ được map (chuyển đổi)
     *    thành Optional<User> bằng cách sử dụng UserMapper.
     * 3. Nếu không tìm thấy, một Optional rỗng sẽ được trả về.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> entityOptional = jpaRepository.findByEmail(email.toLowerCase().trim());

        // Sử dụng phương thức map của Optional để chuyển đổi một cách an toàn
        return entityOptional.map(userMapper::toDomain);
    }
    /**
     * Tìm kiếm một User aggregate bằng ID.
     * 1. Sử dụng JpaRepository để tìm UserEntity trong DB.
     * 2. Nếu tìm thấy, kết quả Optional<UserEntity> sẽ được map
     */
    @Override
    public Optional<User> findById(UUID userId) {
        Optional<UserEntity> entityOptional = jpaRepository.findById(userId);
        return entityOptional.map(userMapper::toDomain);
    }

    /**
     * Kiểm tra xem một email đã tồn tại trong database hay chưa.
     * Phương thức này được ủy quyền (delegate) trực tiếp cho JpaRepository,
     * vì nó được tối ưu hóa để chỉ thực hiện một câu lệnh EXISTS hiệu quả trong DB.
     */
    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email.toLowerCase().trim());
    }

    // Chúng ta có thể thêm findById nếu cần
    // @Override
    // public Optional<User> findById(UUID id) {
    //     return jpaRepository.findById(id).map(userMapper::toDomain);
    // }
}