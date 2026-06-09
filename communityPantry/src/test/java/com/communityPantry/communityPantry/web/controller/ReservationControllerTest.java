package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.*;
import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;
import com.communityPantry.communityPantry.domain.enums.ReservationStatus;
import com.communityPantry.communityPantry.domain.enums.SystemRole;
import com.communityPantry.communityPantry.dto.Reservation.ReservationResponse;
import com.communityPantry.communityPantry.dto.Reservation.ReservationRequest;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.FoodItemRepository;
import com.communityPantry.communityPantry.repository.ReservationRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "jwt.secret=V7FFekAEKlD33bhY2zfoCsLOPcreeQHR53eahcdKd7c"
})
@ActiveProfiles("test")
@Transactional
public class ReservationControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private ObjectMapper objectMapper;

    private User testUser;
    private UserProfile testUserProfile;
    private Community testCommunity;
    private FoodItem testFoodItem;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
        //clear all repositories
        reservationRepository.deleteAll();
        foodItemRepository.deleteAll();
        communityRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();
        //setup a user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole(SystemRole.USER);
        testUser = userRepository.save(testUser);
        //setup a user profile
        testUserProfile = new UserProfile();
        testUserProfile.setUser(testUser);
        testUserProfile.setEmail("test@example.com");
        testUserProfile.setLocation("Test Location");
        testUserProfile = userProfileRepository.save(testUserProfile);
        //setup a community
        testCommunity = new Community();
        testCommunity.setName("Test Community");
        testCommunity.setLocation("Test Location");
        testCommunity = communityRepository.save(testCommunity);
        //setup a food item
        testFoodItem = new FoodItem();
        testFoodItem.setName("Test Food");
        testFoodItem.setQuantity(10);
        testFoodItem.setExpiry(LocalDate.now().plusDays(5));
        testFoodItem.setStatus(FoodItemStatus.AVAILABLE);
        testFoodItem.setCommunity(testCommunity);
        testFoodItem = foodItemRepository.save(testFoodItem);
    }
    //test to create a reservation
    @Test
    @WithMockUser(username = "testuser")
    void testCreateReservation() throws Exception {
        //create a reservation request
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(testFoodItem.getId());
        request.setUserProfileId(testUserProfile.getId());
        request.setQuantity(2);
        //create a reservation using the controller
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.foodItemId", is(testFoodItem.getId().intValue())))
            .andExpect(jsonPath("$.quantity", is(2)))
            .andExpect(jsonPath("$.reservationStatus", is("PENDING")));
        //verify that reservation exists and its parameters are correct
        List<Reservation> reservations = reservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(2, reservations.get(0).getQuantity());
        assertEquals(LocalDate.now(), reservations.get(0).getReservationDate());
        //verify that foodItem exists and its parameters are correct
        List<FoodItem> foodItems = foodItemRepository.findAll();
        assertEquals(1, foodItems.size());
        assertEquals(8, foodItems.get(0).getQuantity());
    }
    //test to get reservations by user
    @Test
    @WithMockUser(username = "testuser")
    void testGetReservationsByUsername() throws Exception {
        //create a reservation request
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(testFoodItem.getId());
        request.setUserProfileId(testUserProfile.getId());
        request.setQuantity(3);
        //create a reservation using the controller
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        //get reservations for the user
        mockMvc.perform(get("/api/reservations/user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].quantity", is(3)));
        //verify that reservation exists and its parameters are correct
        List<Reservation> reservations = reservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(3, reservations.get(0).getQuantity());
        assertEquals(LocalDate.now(), reservations.get(0).getReservationDate());
    }
    //test to get reservations by user
    @Test
    @WithMockUser(username = "testuser")
    void testGetReservationsByUsernameAndStatus() throws Exception {
        //create a reservation request
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(testFoodItem.getId());
        request.setUserProfileId(testUserProfile.getId());
        request.setQuantity(3);
        //create a reservation using the controller
        mockMvc.perform(post("/api/reservations")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        //get reservations for the user
        mockMvc.perform(get("/api/reservations/user/status/PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].quantity", is(3)));
        //verify that reservation exists and its parameters are correct
        //verify that reservation exists and its parameters are correct
        List<Reservation> reservations = reservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(3, reservations.get(0).getQuantity());
        assertEquals(LocalDate.now(), reservations.get(0).getReservationDate());
    }
    //test to cancel a reservation
    @Test
    @WithMockUser(username = "testuser")
    void testCancelReservation() throws Exception {
        //create a reservation request
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(testFoodItem.getId());
        request.setUserProfileId(testUserProfile.getId());
        request.setQuantity(1);
        //create a reservation using the controller
        String responseJson = mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        //get the reservation id
        ReservationResponse reservationResponse = objectMapper.readValue(responseJson, ReservationResponse.class);
        Long reservationId = reservationResponse.getId();

        //cancel the reservation
        mockMvc.perform(patch("/api/reservations/" + reservationId + "/cancellation")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk());
        //verify that reservation exists and its parameters are correct
        List<Reservation> reservations = reservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.get(0).getQuantity());
        //verify that foodItem exists and its parameters are correct following cancellation
        List<FoodItem> foodItems = foodItemRepository.findAll();
        assertEquals(1, foodItems.size());
        assertEquals(10, foodItems.get(0).getQuantity());
    }
    //test to complete a reservation
    @Test
    @WithMockUser(username = "testuser")
    void testCompleteReservation() throws Exception {
        //create a reservation request
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(testFoodItem.getId());
        request.setUserProfileId(testUserProfile.getId());
        request.setQuantity(1);
        //create a reservation using the controller
        String responseJson = mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        //get the reservation id
        ReservationResponse reservationResponse = objectMapper.readValue(responseJson, ReservationResponse.class);
        Long reservationId = reservationResponse.getId();

        //complete the reservation
        mockMvc.perform(patch("/api/reservations/" + reservationId + "/completion")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationStatus", is("COMPLETED")));
        //verify that reservation exists and its parameters are correct
        List<Reservation> reservations = reservationRepository.findAll();
        assertEquals(1, reservations.size());
        assertEquals(1, reservations.get(0).getQuantity());
        //verify that foodItem exists and its parameters are correct following cancellation
        List<FoodItem> foodItems = foodItemRepository.findAll();
        assertEquals(1, foodItems.size());
        assertEquals(9, foodItems.get(0).getQuantity());
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReservationInsufficientQuantity() throws Exception {
        // Create a reservation request with a quantity more than the available amount
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(testFoodItem.getId());
        request.setUserProfileId(testUserProfile.getId());
        request.setQuantity(20);

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", containsString("Not enough quantity available")));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testCreateReservationInvalidQuantity() throws Exception {
        // Create a reservation request with an invalid quantity
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(testFoodItem.getId());
        request.setUserProfileId(testUserProfile.getId());
        request.setQuantity(0);

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors.quantity[0]", containsString("Quantity must be at least 1")));
    }

    @Test
    @WithMockUser(username = "otheruser")
    void testCancelAnotherUserReservation() throws Exception {
        //setup another user
        User otherUser = new User();
        otherUser.setUsername("otheruser");
        otherUser.setPassword("password");
        otherUser.setRole(SystemRole.USER);
        userRepository.save(otherUser);

        UserProfile otherUserProfile = new UserProfile();
        otherUserProfile.setUser(otherUser);
        otherUserProfile.setEmail("other@example.com");
        otherUserProfile.setLocation("Other Location");
        userProfileRepository.save(otherUserProfile);

        //create a reservation for the first user
        Reservation reservation = new Reservation();
        reservation.setFoodItem(testFoodItem);
        reservation.setUserProfile(testUserProfile);
        reservation.setQuantity(1);
        reservation.setReservationDate(LocalDate.now());
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation = reservationRepository.save(reservation);

        //attempt to cancel the reservation as the other user ...
        mockMvc.perform(patch("/api/reservations/" + reservation.getId() + "/cancellation")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message", containsString("Reservation does not belong to user")));
    }
}
