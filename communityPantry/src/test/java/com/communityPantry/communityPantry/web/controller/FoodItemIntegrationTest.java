package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.*;
import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;
import com.communityPantry.communityPantry.domain.enums.SystemRole;
import com.communityPantry.communityPantry.dto.fooditem.FoodItemRequest;
import com.communityPantry.communityPantry.repository.*;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * INTEGRATION TEST
 * Tests the full flow from controller → service → repository → database:
 */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "jwt.secret=test-secretudewgudgywgufdwyuifwgyiwgduidguidguiegfuigieuwwufgwieufgei"
})
@ActiveProfiles("test")
@Transactional // resets DB after each test
public class FoodItemIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private ObjectMapper objectMapper;

    private User testUser;
    private UserProfile testUserProfile;
    private Community testCommunity;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // builds MockMvc with full Spring + security
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // clear DB - delete in correct order to respect foreign key constraints
        reservationRepository.deleteAll();
        foodItemRepository.deleteAll();
        tagRepository.deleteAll();
        communityRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();

        // create user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole(SystemRole.USER);
        testUser = userRepository.save(testUser);

        // create user profile
        testUserProfile = new UserProfile();
        testUserProfile.setUser(testUser);
        testUserProfile.setEmail("test@example.com");
        testUserProfile.setLocation("Birmingham");
        testUserProfile = userProfileRepository.save(testUserProfile);

        // create community
        testCommunity = new Community();
        testCommunity.setName("Test Community");
        testCommunity.setLocation("Birmingham");
        testCommunity = communityRepository.save(testCommunity);

        // create tag
        testTag = new Tag("vegan");
        testTag = tagRepository.save(testTag);
    }

    /**
     * TEST: Create food item via API
     */
    @Test
    @WithMockUser(username = "testuser")
    void createFoodItem() throws Exception {

        FoodItemRequest request = new FoodItemRequest();
        request.setName("Pasta");
        request.setDescription("Creamy pasta");
        request.setQuantity(2);
        request.setExpiry(LocalDate.now().plusDays(3));
        request.setCommunityId(testCommunity.getId());
        request.setUserProfileId(testUserProfile.getId());
        request.setTagIds(Set.of(testTag.getId()));

        mockMvc.perform(post("/food-items")
                .with(csrf()) // required for POST
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Pasta")))
            .andExpect(jsonPath("$.status", is("AVAILABLE")));

        // verify DB
        List<FoodItem> items = foodItemRepository.findAll();
        assertEquals(1, items.size());
        assertEquals("Pasta", items.get(0).getName());
    }

    /**
     * TEST: Get all food items
     */
    @Test
    @WithMockUser(username = "testuser")
    void getAllFoodItems() throws Exception {

        FoodItem item = new FoodItem();
        item.setName("Soup");
        item.setQuantity(1);
        item.setExpiry(LocalDate.now().plusDays(2));
        item.setStatus(FoodItemStatus.AVAILABLE);
        item.setCommunity(testCommunity);
        item.setUserProfile(testUserProfile);
        item.setTags(Set.of(testTag));
        
        // Maintain bidirectional relationship
        testTag.getFoodItems().add(item);

        foodItemRepository.save(item);

        mockMvc.perform(get("/food-items"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("Soup")));
    }

    /**
     * TEST: Search by name
     */
    @Test
    @WithMockUser(username = "testuser")
    void searchFoodItemsByName() throws Exception {

        FoodItem item = new FoodItem();
        item.setName("Curry");
        item.setQuantity(3);
        item.setExpiry(LocalDate.now().plusDays(4));
        item.setStatus(FoodItemStatus.AVAILABLE);
        item.setCommunity(testCommunity);
        item.setUserProfile(testUserProfile);
        item.setTags(Set.of(testTag));
        
        // Maintain bidirectional relationship
        testTag.getFoodItems().add(item);

        foodItemRepository.save(item);

        mockMvc.perform(get("/food-items/search")
                .param("name", "Curry"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name", is("Curry")));
    }

    /**
     * TEST: Filter by tag
     */
    @Test
    @WithMockUser(username = "testuser")
    void searchFoodItemsByTag() throws Exception {

        FoodItem item = new FoodItem();
        item.setName("Vegan Dish");
        item.setQuantity(2);
        item.setExpiry(LocalDate.now().plusDays(3));
        item.setStatus(FoodItemStatus.AVAILABLE);
        item.setCommunity(testCommunity);
        item.setUserProfile(testUserProfile);
        item.setTags(Set.of(testTag));
        
        // Maintain bidirectional relationship
        testTag.getFoodItems().add(item);

        foodItemRepository.save(item);

        mockMvc.perform(get("/food-items/tag")
                .param("tag_ids", String.valueOf(testTag.getId())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("Vegan Dish")));
    }

    /**
     * TEST: Get available items only
     */
    @Test
    @WithMockUser(username = "testuser")
    void getAvailableFoodItems() throws Exception {

        FoodItem available = new FoodItem();
        available.setName("Rice");
        available.setQuantity(1);
        available.setExpiry(LocalDate.now().plusDays(2));
        available.setStatus(FoodItemStatus.AVAILABLE);
        available.setCommunity(testCommunity);
        available.setUserProfile(testUserProfile);

        FoodItem expired = new FoodItem();
        expired.setName("Old Bread");
        expired.setQuantity(1);
        expired.setExpiry(LocalDate.now().minusDays(1));
        expired.setStatus(FoodItemStatus.EXPIRED);
        expired.setCommunity(testCommunity);
        expired.setUserProfile(testUserProfile);

        foodItemRepository.save(available);
        foodItemRepository.save(expired);

        mockMvc.perform(get("/food-items/available"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].status", is("AVAILABLE")));
    }
}
