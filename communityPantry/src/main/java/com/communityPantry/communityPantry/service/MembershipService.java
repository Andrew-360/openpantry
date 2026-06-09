package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.enums.CommunityRole;
import com.communityPantry.communityPantry.exception.*;
import org.springframework.security.core.Authentication;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.MembershipRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import com.communityPantry.communityPantry.domain.enums.SystemRole;
import com.communityPantry.communityPantry.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final CommunityRepository communityRepository;
    private final UserProfileRepository userProfileRepository;

    public MembershipService(
            MembershipRepository membershipRepository,
            CommunityRepository communityRepository,
            UserProfileRepository userProfileRepository) {
        this.membershipRepository = membershipRepository;
        this.communityRepository = communityRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public Membership createMembership(Long communityId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidCredentialsException("User is not authenticated");
        }

        UserProfile userProfile = getUserProfileFromAuthentication(authentication);

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found: " + communityId));

        LocalDate joinDate = LocalDate.now(); // Set join date to current date
        Membership membership = Membership.create(CommunityRole.MEMBER, joinDate, false, community, userProfile);
        return membershipRepository.save(membership);
    }

    private boolean isCommunityModerator(Community community, UserProfile userProfile) {
        Optional<Membership> currentUserMembership = getMembershipByCommunityAndUserProfile(
                community.getId(), userProfile.getId());

        if (currentUserMembership.isEmpty()) {
            return false; // Not a member
        }
        CommunityRole currentUserRole = currentUserMembership.get().getRole();

        return currentUserRole == CommunityRole.MODERATOR;
    }

    public UserProfile getUserProfileFromAuthentication(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        if (currentUser == null)
            throw new InvalidCredentialsException("User is not authenticated");

        return userProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));
    }

    public boolean checkDeletePrivileges(Authentication authentication, Long communityId, Long userProfileId) {
        /* Checks if a user is able to delete a membership
         * One of these must be true:
         * - User is a system admin
         * - User is trying to delete their own membership
         * - User is a community moderator in the community where the membership is being deleted
         */

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found: " + communityId));
        UserProfile userProfile = userProfileRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("UserProfile not found: " + userProfileId));
        UserProfile currentUserProfile = getUserProfileFromAuthentication(authentication);

        if (userProfile.getId().equals(currentUserProfile.getId())) {
            return true; // Users can delete their own membership
        }
        // Get current users membership in this community
        return isCommunityModerator(community, currentUserProfile);
    }

    public boolean checkUpdatePrivileges(Authentication authentication, Long communityId) {
        // Must be admin or community moderator
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        UserProfile userProfile = getUserProfileFromAuthentication(authentication);
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found: " + communityId));

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> SystemRole.ADMIN.name().equals(auth.getAuthority()));
        return isAdmin || isCommunityModerator(community, userProfile);
    }

    public List<Membership> getMembershipsByCommunity(Long communityId) {
        return membershipRepository.findByCommunityId(communityId);
    }

    public List<Membership> getMembershipsByUserProfile(Long userProfileId) {
        return membershipRepository.findByUserProfileId(userProfileId);
    }

    public Optional<Membership> getMembershipByCommunityAndUserProfile(Long communityId, Long userProfileId) {
        List<Membership> communityMemberships = membershipRepository.findByCommunityId(communityId);
        for (Membership membership : communityMemberships) {
            if (membership.getUserProfile().getId().equals(userProfileId)) {
                return Optional.of(membership);
            }
        }
        return Optional.empty();
    }

    public Membership updateMembership(Authentication authentication, Long communityId, Long userProfileId, Map<String, Object> membershipDetails) {
        if (checkUpdatePrivileges(authentication, communityId))
            throw new InvalidCredentialsException("Insufficient Permission to update membership");

        Membership membership = getMembershipByCommunityAndUserProfile(communityId, userProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Membership not found for communityId: " + communityId
                        + " and userProfileId: " + userProfileId));

        if (membershipDetails.containsKey("role")) {
            try {
                String roleText = (String) membershipDetails.get("role");
                roleText = roleText.toUpperCase();
                CommunityRole role = CommunityRole.valueOf(roleText);
                membership.setRole(role);
            } catch (RuntimeException ex) {
                // TODO: change this to use a DTO
                throw new IllegalArgumentException("Invalid role format");
            }
        }

        if (membershipDetails.containsKey("isBanned")) {
            String isBanned =  (String) membershipDetails.get("isBanned");
            membership.setIsBanned(isBanned.equalsIgnoreCase("true"));
        }

        return membershipRepository.save(membership);
    }

    public void deleteMembership(Authentication authentication, Long communityId, Long userProfileId) {
        if (!checkDeletePrivileges(authentication, userProfileId, communityId))
            throw new InvalidCredentialsException("User does not have permission to delete this membership");

        Membership member = getMembershipByCommunityAndUserProfile(communityId, userProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Membership not found for communityId: " + communityId
                        + " and userProfileId: " + userProfileId));
        membershipRepository.deleteById(member.getId());
    }

    public void leaveCommunity(Authentication authentication, Long communityId) {
        UserProfile currentUserProfile = getUserProfileFromAuthentication(authentication);
        Membership member = getMembershipByCommunityAndUserProfile(communityId, currentUserProfile.getId())
                .orElseThrow(() -> new EntityNotFoundException("Membership not found for communityId: " + communityId
                        + " and userProfileId: " + currentUserProfile.getId()));
        membershipRepository.deleteById(member.getId());
    }
}
