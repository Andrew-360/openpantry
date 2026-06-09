package com.communityPantry.communityPantry.repository;

import com.communityPantry.communityPantry.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Boolean existsByEmail(String email);
    // Spring Data JPA will automatically implement these methods based on the
    // method name

    Optional<UserProfile> findByUserId(Long userId);

    Optional<Long> findIdByUserUsername(String username);

    Boolean existsByUserUsername(String username);

    Optional<UserProfile> findByUserUsername(String username);
}
