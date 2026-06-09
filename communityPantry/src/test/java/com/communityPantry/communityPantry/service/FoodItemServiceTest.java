package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.domain.FoodItem;
import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.mapper.FoodItemMapper;
import com.communityPantry.communityPantry.repository.FoodItemRepository;
import com.communityPantry.communityPantry.repository.TagRepository;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;

import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;
import com.communityPantry.communityPantry.dto.fooditem.FoodItemRequest;
import com.communityPantry.communityPantry.dto.fooditem.FoodItemResponse;

import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.repository.UserRepository;
import com.communityPantry.communityPantry.exception.AlreadyExpiredException;
import com.communityPantry.communityPantry.exception.InsufficientQuantityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Service unit tests
 * Mocks DB dependencies and tests the business logic within the service layer
 */
@ExtendWith(MockitoExtension.class)
public class FoodItemServiceTest {

    // fake repository dependencies
    @Mock
    private UserRepository user_repository;
    @Mock
    private FoodItemRepository food_item_repository;
    @Mock
    private UserProfileRepository user_profile_repository;
    @Mock
    private CommunityRepository community_repository;
    @Mock
    private TagRepository tag_repository;
    @Mock
    private FoodItemMapper food_item_mapper;

    @InjectMocks
    private FoodItemService food_item_service; // fake service

    /**
     * tests the service logic required to build a food item entity and
     * save it to the database
     */
    @Test
    void createFoodItem() {
        // create fake API request DTO
        FoodItemRequest request = new FoodItemRequest();
        request.setName("Cottage Pie");
        request.setDescription("Ground beef and vegetables");
        request.setQuantity(3);
        request.setExpiry(LocalDate.now().plusDays(3));
        request.setUserProfileId(8L);
        request.setCommunityId(4L);
        request.setTagIds(Set.of(21L));

        // fake related entities
        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername("andrew");

        UserProfile user_profile = new UserProfile();
        user_profile.setId(8L);
        user_profile.setUser(authenticatedUser);

        Community community = new Community();
        community.setId(4L);
        Tag tag = new Tag("Beef");
        tag.setId(21L);

        FoodItem saved_food_item = new FoodItem();
        saved_food_item.setName("Cottage Pie");
        saved_food_item.setStatus(FoodItemStatus.AVAILABLE);

        // create fake API response DTO
        FoodItemResponse response = new FoodItemResponse();
        response.setName("Cottage Pie");
        response.setStatus(FoodItemStatus.AVAILABLE);

        when(user_repository.findByUsername("andrew")).thenReturn(Optional.of(authenticatedUser));
        when(user_profile_repository.findByUserId(authenticatedUser.getId())).thenReturn(Optional.of(user_profile));
        when(community_repository.findCommunityById(4L)).thenReturn(Optional.of(community));
        when(tag_repository.findByIdIn(Set.of(21L))).thenReturn(Set.of(tag));
        // mocking successful mapping of request DTO to a food item entity
        when(food_item_mapper.toEntity(request)).thenReturn(saved_food_item);
        // mocking successful saving of the built food item to the DB
        when(food_item_repository.save(any(FoodItem.class))).thenReturn(saved_food_item);
        // mocking successful mapping of food item entity to response DTO
        when(food_item_mapper.toResponse(saved_food_item)).thenReturn(response);

        FoodItemResponse result = food_item_service.createFoodItem("andrew", request);

        // checks expected availability and name == actual values
        assertEquals("Cottage Pie", result.getName());
        assertEquals(FoodItemStatus.AVAILABLE, result.getStatus());
        // checks mocked method was actually called
        verify(food_item_repository).save(argThat(food_item -> food_item.getName().equals("Cottage Pie")));
    }

    /**
     * tests the logic required to return all available items from the database
     */
    @Test
    void getAvailableFoodItems() {
        FoodItem food_item_entity = new FoodItem();
        food_item_entity.setStatus(FoodItemStatus.AVAILABLE);

        // create fake API response DTO
        FoodItemResponse response = new FoodItemResponse();
        response.setStatus(FoodItemStatus.AVAILABLE);

        // mocking successful DB lookups of food items using status
        when(food_item_repository.findByStatus(FoodItemStatus.AVAILABLE)).thenReturn(List.of(food_item_entity));
        // mocking successful mapping of food item entity to response DTO
        when(food_item_mapper.toResponse(food_item_entity)).thenReturn(response);

        List<FoodItemResponse> result = food_item_service.getAvailableFoodItems();

        // checks expected availability and number of food items returned == actual
        // values
        assertEquals(1, result.size());
        assertEquals(FoodItemStatus.AVAILABLE, result.get(0).getStatus());
    }

    /**
     * tests the logic required to return all food items from the database
     */
    @Test
    void getAllFoodItems() {
        // create fake food item entity
        FoodItem food_item_entity = new FoodItem();
        food_item_entity.setName("Burrito Bowl");
        // create fake API response DTO
        FoodItemResponse response = new FoodItemResponse();
        response.setName("Burrito Bowl");

        // mocking successful DB lookups of all food items
        when(food_item_repository.findAll()).thenReturn(List.of(food_item_entity));
        // mocking successful mapping of food item entity to response DTO
        when(food_item_mapper.toResponse(food_item_entity)).thenReturn(response);

        List<FoodItemResponse> result = food_item_service.getAllFoodItems();

        // checks expected number of food items returned and name == actual values
        assertEquals(1, result.size());
        assertEquals("Burrito Bowl", result.get(0).getName());
    }

    /**
     * test the logic required to return food items that don't
     * expire before a specified deadline
     */
    @Test
    void getFoodItemByDate() {
        LocalDate deadline = LocalDate.now().plusDays(5);

        FoodItem food_item_entity = new FoodItem();
        // create fake API request DTO
        FoodItemResponse response = new FoodItemResponse();
        response.setName("Veggie Shepherd's Pie");

        // mocking successful DB lookups of food items using expiry date
        when(food_item_repository.findByExpiryDateLessThanEqual(deadline)).thenReturn(List.of(food_item_entity));
        // mocking successful mapping of food item entity to response DTO
        when(food_item_mapper.toResponse(food_item_entity)).thenReturn(response);

        List<FoodItemResponse> result = food_item_service.getFoodItemByDate(deadline);

        // checks expected number of food items returned and name == actual values
        assertEquals(1, result.size());
        assertEquals("Veggie Shepherd's Pie", result.get(0).getName());
    }

    /**
     * tests expiry date validation
     */
    @Test
    void createFoodItemWithInvalidExpiry() {
        // create fake API request with an invalid expiry date
        FoodItemRequest request = new FoodItemRequest();
        request.setName("Expired Milk");
        request.setQuantity(2);
        request.setExpiry(LocalDate.now().minusDays(1));
        request.setCommunityId(4L);

        Community community = new Community();
        community.setId(4L);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername("andrew");

        when(user_repository.findByUsername("andrew")).thenReturn(Optional.of(authenticatedUser));
        when(community_repository.findCommunityById(4L)).thenReturn(Optional.of(community));

        AlreadyExpiredException ex = assertThrows(AlreadyExpiredException.class,
                () -> food_item_service.createFoodItem("andrew", request));

        assertEquals("Food item has already expired", ex.getMessage());
    }

    /**
     * tests quantity validation
     */
    @Test
    void createFoodItemWithInvalidQuantity() {
        // create fake API request with an invalid quantity (edge case)
        FoodItemRequest request = new FoodItemRequest();
        request.setName("Zero Quantity Item");
        request.setQuantity(0);
        request.setExpiry(LocalDate.now().plusDays(2));
        request.setCommunityId(4L);

        Community community = new Community();
        community.setId(4L);

        User authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setUsername("andrew");

        when(user_repository.findByUsername("andrew")).thenReturn(Optional.of(authenticatedUser));
        when(community_repository.findCommunityById(4L)).thenReturn(Optional.of(community));

        InsufficientQuantityException ex = assertThrows(InsufficientQuantityException.class,
                () -> food_item_service.createFoodItem("andrew", request));

        assertEquals("Quantity must be greater than 0", ex.getMessage());
    }
}
