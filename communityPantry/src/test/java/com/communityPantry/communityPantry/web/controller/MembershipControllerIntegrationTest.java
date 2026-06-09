package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.enums.CommunityRole;
import com.communityPantry.communityPantry.domain.enums.SystemRole;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.MembershipRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
Contains integration tests for the MembershipController,
Checks the process from sending API call to getting response and checking correct action was taken
Tests are currently run with h2 database in memory
 */

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "jwt.secret=veryveryveryverylongjwtsecrettestkey123456"
})
@ActiveProfiles("test")
@Transactional
class MembershipControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private MockMvc mockMvc;
    private User actorUser;
    private UserProfile actorUserProfile;
    private Community community;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        actorUser = createUser("membership_actor", "membership-actor@example.com");
        actorUserProfile = userProfileRepository.findByUserId(actorUser.getId()).orElseThrow();

        community = new Community();
        community.setName("Membership Integration Community");
        community.setLocation("Birmingham");
        community.setDescription("Community used for membership controller integration tests");
        community = communityRepository.save(community);
    }

    @Test
    void createMembership_updatesDatabase() throws Exception {
        mockMvc.perform(post("/api/memberships/community/{communityId}", community.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(actorUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.communityId", is(community.getId().intValue())))
                .andExpect(jsonPath("$.role", is("MEMBER")))
                .andExpect(jsonPath("$.isBanned", is(false)));

        List<Membership> memberships = membershipRepository.findByCommunityId(community.getId());
        assertEquals(1, memberships.size());
        Membership savedMembership = memberships.get(0);
        assertEquals(actorUserProfile.getId(), savedMembership.getUserProfile().getId());
        assertEquals(CommunityRole.MEMBER, savedMembership.getRole());
        assertFalse(savedMembership.getIsBanned());
    }

    @Test
    void getMembershipsByCommunity_returnsMemberships() throws Exception {
        User memberTwo = createUser("membership_member_two", "membership-member-two@example.com");
        UserProfile memberTwoProfile = userProfileRepository.findByUserId(memberTwo.getId()).orElseThrow();

        Membership firstMembership = Membership.create(
                CommunityRole.MEMBER,
                LocalDate.now(),
                false,
                community,
                actorUserProfile);
        membershipRepository.save(firstMembership);

        Membership secondMembership = Membership.create(
                CommunityRole.MODERATOR,
                LocalDate.now(),
                false,
                community,
                memberTwoProfile);
        membershipRepository.save(secondMembership);

        mockMvc.perform(get("/api/memberships/community/{communityId}", community.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(actorUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].communityId", everyItem(is(community.getId().intValue()))));
    }

    @Test
    void updateMembership_membershipDoesntExist() throws Exception {
        String updateBody = "{\"role\": \"MODERATOR\", \"isBanned\": \"true\"}";

        mockMvc.perform(patch("/api/memberships/community/{communityId}/user/{userProfileId}", community.getId(), 999999L)
                        .with(SecurityMockMvcRequestPostProcessors.user(actorUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound());

        assertTrue(membershipRepository.findByCommunityId(community.getId()).isEmpty());
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRole(SystemRole.USER);
        user = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setEmail(email);
        profile.setLocation("Birmingham");
        userProfileRepository.save(profile);

        return user;
    }
}

