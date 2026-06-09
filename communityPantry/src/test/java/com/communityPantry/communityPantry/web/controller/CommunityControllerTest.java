package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.enums.SystemRole;
import com.communityPantry.communityPantry.dto.community.CommunityDTO;
import com.communityPantry.communityPantry.dto.community.CreateCommunityRequest;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.repository.UserRepository;
import com.communityPantry.communityPantry.repository.ReservationRepository;
import com.communityPantry.communityPantry.repository.FoodItemRepository;
import com.communityPantry.communityPantry.service.CommunityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "jwt.secret=dGVzdFNlY3JldEtleUZvclRlc3RpbmdPbmx5MTIzNDU2Nzg=",
    "jwt.expiration=86400000",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
public class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UsernamePasswordAuthenticationToken mockAuth;
    private static final String TEST_USERNAME = "testuser";

    // Set up a test user, profile, and authentication token before each test
    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        foodItemRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(SystemRole.USER);
        User savedUser = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        profile.setEmail("testuser@test.com");
        profile.setLocation("Test Location");
        userProfileRepository.save(profile);

        mockAuth = new UsernamePasswordAuthenticationToken(
                TEST_USERNAME,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    // POST ----- creating communities

    // Should create a community successfully when valid data is provided
    @Test
    void createValidCommunity() throws Exception {
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("Test Community");
        request.setLocation("Test Location");
        request.setDescription("Test Description");

        mockMvc.perform(post("/api/communities")
                .with(authentication(mockAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Community"))
                .andExpect(jsonPath("$.location").value("Test Location"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    // Should return 400 when name is blank
    @Test
    void createBlankCommunity() throws Exception {
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("");
        request.setLocation("Test Location");

        mockMvc.perform(post("/api/communities")
                .with(authentication(mockAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Should return 409 when trying to create a duplicate community (same name + location)
    @Test
    void createDuplicateCommunity() throws Exception {
        // Create the first community directly via service
        CreateCommunityRequest first = new CreateCommunityRequest();
        first.setName("Test Name");
        first.setLocation("Test Location");
        communityService.createCommunity(TEST_USERNAME, first);

        // Attempt to create the same community again via the endpoint
        mockMvc.perform(post("/api/communities")
                .with(authentication(mockAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    // GET ----- getting communities

    // Should get a community by ID successfully when it exists
    @Test
    void getValidCommunityById() throws Exception {
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("Test Community");
        request.setLocation("Test Location");
        CommunityDTO created = communityService.createCommunity(TEST_USERNAME, request);

        mockMvc.perform(get("/api/communities/{id}", created.getId())
                .with(authentication(mockAuth))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()))
                .andExpect(jsonPath("$.name").value("Test Community"))
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    // Should return 404 when trying to get a community that doesn't exist
    @Test
    void getNonExistentCommunityById() throws Exception {
        mockMvc.perform(get("/api/communities/{id}", 999999L)
                .with(authentication(mockAuth))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // Should get all communities in a location successfully when they exist
    @Test
    void getAllCommunitiesInLocation() throws Exception {
        CreateCommunityRequest req1 = new CreateCommunityRequest();
        req1.setName("Test Community A");
        req1.setLocation("Test Location A");
        communityService.createCommunity(TEST_USERNAME, req1);

        CreateCommunityRequest req2 = new CreateCommunityRequest();
        req2.setName("Test Community B");
        req2.setLocation("Test Location A");
        communityService.createCommunity(TEST_USERNAME, req2);

        mockMvc.perform(get("/api/communities")
                .with(authentication(mockAuth))
                .param("location", "Test Location A")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getAllCommunities() throws Exception {
        CreateCommunityRequest req1 = new CreateCommunityRequest();
        req1.setName("Test Community C");
        req1.setLocation("Test Location C");
        communityService.createCommunity(TEST_USERNAME, req1);

        mockMvc.perform(get("/api/communities")
                .with(authentication(mockAuth))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // PUT ----- updating communities

    // Should update a community successfully when valid data is provided and community exists
    @Test
    void updateValidCommunity() throws Exception {
        CreateCommunityRequest createRequest = new CreateCommunityRequest();
        createRequest.setName("Old Community");
        createRequest.setLocation("Old Location");
        createRequest.setDescription("Old Description");
        CommunityDTO created = communityService.createCommunity(TEST_USERNAME, createRequest);

        CreateCommunityRequest updateRequest = new CreateCommunityRequest();
        updateRequest.setName("New Community");
        updateRequest.setLocation("New Location");
        updateRequest.setDescription("New Description");

        mockMvc.perform(put("/api/communities/{id}", created.getId())
                .with(authentication(mockAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Community"))
                .andExpect(jsonPath("$.location").value("New Location"))
                .andExpect(jsonPath("$.description").value("New Description"));
    }

    // Should return 404 when trying to update a community that doesn't exist
    @Test
    void updateNonExistentCommunity() throws Exception {
        CreateCommunityRequest updateRequest = new CreateCommunityRequest();
        updateRequest.setName("Test Community");
        updateRequest.setLocation("Test Location");

        mockMvc.perform(put("/api/communities/{id}", 999999L)
                .with(authentication(mockAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    // DELETE ----- deleting communities

    // Should delete a community successfully when it exists
    @Test
    void deleteValidCommunity() throws Exception {
        CreateCommunityRequest createRequest = new CreateCommunityRequest();
        createRequest.setName("Test Community");
        createRequest.setLocation("Test Location");
        CommunityDTO created = communityService.createCommunity(TEST_USERNAME, createRequest);

        mockMvc.perform(delete("/api/communities/{id}", created.getId())
                .with(authentication(mockAuth)))
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/communities/{id}", created.getId())
                .with(authentication(mockAuth)))
                .andExpect(status().isNotFound());
    }

    // Should return 404 when trying to delete a community that doesn't exist
    @Test
    void deleteNonExistentCommunity() throws Exception {
        mockMvc.perform(delete("/api/communities/{id}", 999999L)
                .with(authentication(mockAuth)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}