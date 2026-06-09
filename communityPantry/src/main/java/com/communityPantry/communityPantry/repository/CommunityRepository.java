package com.communityPantry.communityPantry.repository;

import com.communityPantry.communityPantry.domain.Community;
//import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndLocation(String name, String location);

    Page<Community> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Community> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    Page<Community> findByNameContainingIgnoreCaseAndLocationContainingIgnoreCase(String name, String location,
            Pageable pageable);

    Optional<Community> findCommunityById(Long id);
}
