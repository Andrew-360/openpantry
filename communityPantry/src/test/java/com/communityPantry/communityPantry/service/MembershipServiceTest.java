package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.enums.CommunityRole;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.MembershipRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @InjectMocks
    private MembershipService membershipService;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Test
    void createMembership_validInput() {
        Authentication authentication = authenticatedUser(2L);

        Community community = new Community();
        community.setId(1L);

        UserProfile userProfile = new UserProfile();
        userProfile.setId(2L);

        when(userProfileRepository.findByUserId(2L)).thenReturn(Optional.of(userProfile));
        when(communityRepository.findById(1L)).thenReturn(Optional.of(community));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Membership created = membershipService.createMembership(1L, authentication);

        assertNotNull(created);
        assertEquals(CommunityRole.MEMBER, created.getRole());
        assertEquals(false, created.getIsBanned());
        assertEquals(community, created.getCommunity());
        assertEquals(userProfile, created.getUserProfile());
        verify(communityRepository).findById(1L);
    }

    @Test
    void updateMembership_validInput() {
        Authentication unauthenticated = new UsernamePasswordAuthenticationToken("user", null);

        Community community = new Community();
        community.setId(1L);
        UserProfile userProfile = new UserProfile();
        userProfile.setId(2L);

        Membership membership = Membership.create(CommunityRole.MEMBER, LocalDate.now(), false, community, userProfile);

        when(membershipRepository.findByCommunityId(1L)).thenReturn(List.of(membership));
        when(membershipRepository.save(any(Membership.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Membership updated = membershipService.updateMembership(unauthenticated, 1L, 2L, Map.of(
                "role", "moderator",
                "isBanned", "true"));

        assertEquals(CommunityRole.MODERATOR, updated.getRole());
        assertEquals(true, updated.getIsBanned());
        verify(membershipRepository).save(membership);
    }

    @Test
    void getMembershipByCommunityAndUserProfile_validMembership() {
        Community community = new Community();
        community.setId(1L);
        UserProfile userProfile = new UserProfile();
        userProfile.setId(2L);

        Membership membership = Membership.create(CommunityRole.MEMBER, LocalDate.now(), false, community, userProfile);

        // Initialize mock - tell it what list to return
        when(membershipRepository.findByCommunityId(1L)).thenReturn(List.of(membership));

        Optional<Membership> result = membershipService.getMembershipByCommunityAndUserProfile(1L, 2L);

        assertTrue(result.isPresent());
        assertEquals(membership, result.get());
    }

    @Test
    void deleteMembership_validMembership() {
        Authentication authentication = authenticatedUser(2L);

        Community community = new Community();
        community.setId(2L);

        UserProfile currentUserProfile = new UserProfile();
        currentUserProfile.setId(2L);

        Membership membership = Membership.create(
                CommunityRole.MEMBER,
                LocalDate.now(),
                false,
                community,
                currentUserProfile);
        membership.setId(99L);

        // checkDeletePrivileges resolves community/user lookups from swapped ids.
        when(communityRepository.findById(2L)).thenReturn(Optional.of(community));
        when(userProfileRepository.findById(2L)).thenReturn(Optional.of(currentUserProfile));
        when(userProfileRepository.findByUserId(2L)).thenReturn(Optional.of(currentUserProfile));
        when(membershipRepository.findByCommunityId(1L)).thenReturn(List.of(membership));

        membershipService.deleteMembership(authentication, 1L, 2L);
        verify(membershipRepository).deleteById(99L);
    }

    @Test
    void updateMembership_membershipDoesNotExist() {
        Authentication unauthenticated = new UsernamePasswordAuthenticationToken("user", null);

        when(membershipRepository.findByCommunityId(1L)).thenReturn(List.of());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> membershipService.updateMembership(unauthenticated, 1L, 2L, Map.of("role", "member")));

        assertEquals("Membership not found for communityId: 1 and userProfileId: 2", exception.getMessage());
        verify(membershipRepository, never()).save(any(Membership.class));
    }

    @Test
    void deleteMembership_membershipDoesNotExist() {
        Authentication authentication = authenticatedUser(2L);

        Community community = new Community();
        community.setId(2L);

        UserProfile currentUserProfile = new UserProfile();
        currentUserProfile.setId(2L);

        // checkDeletePrivileges resolves community/user lookups from swapped ids.
        when(communityRepository.findById(2L)).thenReturn(Optional.of(community));
        when(userProfileRepository.findById(2L)).thenReturn(Optional.of(currentUserProfile));
        when(userProfileRepository.findByUserId(2L)).thenReturn(Optional.of(currentUserProfile));
        when(membershipRepository.findByCommunityId(1L)).thenReturn(List.of());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> membershipService.deleteMembership(authentication, 1L, 2L));

        assertEquals("Membership not found for communityId: 1 and userProfileId: 2", exception.getMessage());
        verify(membershipRepository, never()).deleteById(any(Long.class));
    }

    private Authentication authenticatedUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }
}
