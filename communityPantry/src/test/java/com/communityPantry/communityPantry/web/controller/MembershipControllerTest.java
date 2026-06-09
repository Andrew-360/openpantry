package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.enums.CommunityRole;
import com.communityPantry.communityPantry.exception.InvalidCredentialsException;
import com.communityPantry.communityPantry.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.communityPantry.communityPantry.security.AuthEntryPointJwt;
import com.communityPantry.communityPantry.security.JwtService;
import com.communityPantry.communityPantry.service.UserDetailsServiceImpl;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MembershipController.class)
@AutoConfigureMockMvc(addFilters = false)
class MembershipControllerTest {
    /* Tests the controller to make sure it passes off correctly to the service layer
     & Ensures that only requests with valid authentication are executed  */

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MembershipService membershipService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockitoBean
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    void createMembership_hasPermission() throws Exception {
        Authentication auth = authenticatedUser();
        Membership created = buildTestMembership(false);

        when(membershipService.createMembership(1L, auth)).thenReturn(created);

        mockMvc.perform(post("/api/memberships/community/1")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(membershipService).createMembership(1L, auth);
    }

    @Test
    void createMembership_unauthorised() throws Exception {
        Authentication unauthenticated = new UsernamePasswordAuthenticationToken("user", null);
        when(membershipService.createMembership(eq(1L), eq(unauthenticated)))
                .thenThrow(new InvalidCredentialsException("User is not authenticated"));

        mockMvc.perform(post("/api/memberships/community/1")
                        .principal(unauthenticated)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(membershipService).createMembership(1L, unauthenticated);
    }

    @Test
    void createMembership_authenticationMissing() throws Exception {
        when(membershipService.createMembership(eq(1L), isNull()))
                .thenThrow(new InvalidCredentialsException("User is not authenticated"));

        mockMvc.perform(post("/api/memberships/community/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(membershipService).createMembership(1L, null);
    }

    @Test
    void updateMembership_authenticated() throws Exception {
        Authentication auth = authenticatedUser();
        Membership updated = buildTestMembership(true);

        when(membershipService.updateMembership(eq(auth), eq(1L), eq(2L), ArgumentMatchers.<Map<String, Object>>any())).thenReturn(updated);

        mockMvc.perform(patch("/api/memberships/community/1/user/2")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"MODERATOR\",\"isBanned\":false}"))
                .andExpect(status().isOk());

                        verify(membershipService).updateMembership(eq(auth), eq(1L), eq(2L), ArgumentMatchers.<Map<String, Object>>any());
    }

    @Test
    void updateMembership_unauthorised() throws Exception {
        Authentication auth = authenticatedUser();
        when(membershipService.updateMembership(eq(auth), eq(1L), eq(2L), ArgumentMatchers.<Map<String, Object>>any()))
                .thenThrow(new InvalidCredentialsException("Insufficient Permission to update membership"));

        mockMvc.perform(patch("/api/memberships/community/1/user/2")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"MODERATOR\"}"))
                .andExpect(status().isUnauthorized());

        verify(membershipService).updateMembership(eq(auth), eq(1L), eq(2L), ArgumentMatchers.<Map<String, Object>>any());
    }

    @Test
    void deleteMembership_authorised() throws Exception {
        Authentication auth = authenticatedUser();

        mockMvc.perform(delete("/api/memberships/community/1/user/2")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        verify(membershipService).deleteMembership(auth, 1L, 2L);
    }

    @Test
    void deleteMembership_unauthorised() throws Exception {
        Authentication auth = authenticatedUser();
        doThrow(new InvalidCredentialsException("User does not have permission to delete this membership"))
                .when(membershipService)
                .deleteMembership(eq(auth), eq(1L), eq(2L));

        mockMvc.perform(delete("/api/memberships/community/1/user/2")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(membershipService).deleteMembership(auth, 1L, 2L);
    }

    private Authentication authenticatedUser() {
        // Generates an Authentication object containing a user that links to userprofile with id 2
        UserProfile userProfile = new UserProfile();
        userProfile.setId(2L);

        return new UsernamePasswordAuthenticationToken(
                userProfile,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private Membership buildTestMembership(boolean isModerator) {
        Community community = new Community();
        community.setId(1L);

        UserProfile userProfile = new UserProfile();
        userProfile.setId(2L);

        Membership membership = Membership.create(
                isModerator ? CommunityRole.MODERATOR : CommunityRole.MEMBER,
                LocalDate.of(2026, 1, 1),
                false,
                community,
                userProfile
        );
        membership.setId(5L);
        return membership;
    }
}

