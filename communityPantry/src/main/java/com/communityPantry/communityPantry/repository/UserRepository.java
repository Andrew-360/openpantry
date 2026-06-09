package com.communityPantry.communityPantry.repository;

import com.communityPantry.communityPantry.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA will automatically implement these methods based on the
    // method name

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}
