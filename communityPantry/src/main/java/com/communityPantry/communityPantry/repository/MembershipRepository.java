package com.communityPantry.communityPantry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.communityPantry.communityPantry.domain.Membership;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByCommunityId(Long communityId);

    List<Membership> findByUserProfileId(Long userProfileId);
}
