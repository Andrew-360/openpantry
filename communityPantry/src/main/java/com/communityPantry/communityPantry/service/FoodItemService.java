package com.communityPantry.communityPantry.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.communityPantry.communityPantry.domain.FoodItem;
import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;
import com.communityPantry.communityPantry.dto.fooditem.FoodItemRequest;
import com.communityPantry.communityPantry.dto.fooditem.FoodItemResponse;
import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.exception.AlreadyExpiredException;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.exception.InsufficientQuantityException;
import com.communityPantry.communityPantry.mapper.FoodItemMapper;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.FoodItemRepository;
import com.communityPantry.communityPantry.repository.TagRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contains business logic for food listings/items
 */
@Service // this class belongs in the service layer
@Transactional // manages database transactions
public class FoodItemService {

        private final FoodItemRepository food_item_repository;
        private final UserProfileRepository user_profile_repository;
        private final CommunityRepository community_repository;
        private final TagRepository tag_repository;
        private final FoodItemMapper food_item_mapper;
        private final UserRepository user_repository;

        // dependency injection via constructor
        public FoodItemService(
                        FoodItemRepository food_item_repository,
                        UserProfileRepository user_profile_repository,
                        CommunityRepository community_repository,
                        TagRepository tag_repository,
                        FoodItemMapper food_item_mapper,
                        UserRepository user_repository) {
                this.food_item_repository = food_item_repository;
                this.user_profile_repository = user_profile_repository;
                this.community_repository = community_repository;
                this.tag_repository = tag_repository;
                this.food_item_mapper = food_item_mapper;
                this.user_repository = user_repository;
        }

        /**
         * creates a new food item and saves it to the database securely
         */
        public FoodItemResponse createFoodItem(String username, FoodItemRequest request) {
                // identify the user that wants to post this food item
                User authenticatedUser = user_repository.findByUsername(username)
                                .orElseThrow(() -> new EntityNotFoundException("User not found"));
                // identify the user's community
                Community community = community_repository.findCommunityById(
                                request.getCommunityId())
                                .orElseThrow(() -> new EntityNotFoundException("Community not found"));
                // identify related tags
                Set<Tag> tags = new HashSet<>(tag_repository.findByIdIn(
                                request.getTagIds()));

                if (request.getExpiry().isBefore(LocalDate.now())) {
                        throw new AlreadyExpiredException("Food item has already expired");
                }
                if (request.getQuantity() != null && request.getQuantity() <= 0) {
                        throw new InsufficientQuantityException("Quantity must be greater than 0");
                }

                // build a food item entity using the mapper
                FoodItem food_listing = food_item_mapper.toEntity(request);
                // the initial status of a food listing is AVAILABLE
                food_listing.setStatus(FoodItemStatus.AVAILABLE);
                UserProfile user_profile = user_profile_repository.findByUserId(authenticatedUser.getId())
                                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));
                food_listing.setUserProfile(user_profile);
                food_listing.setCommunity(community);
                food_listing.setTags(tags);
                // no reservation has been made yet
                food_listing.setReservations(null);

                // save to database via repository
                food_item_repository.save(food_listing);

                // create and return response DTO
                return food_item_mapper.toResponse(food_listing);
        }

        /**
         * returns all food items in the database
         */
        @Transactional(readOnly = true) // avoids unnecessary locks and flushing operations
        public List<FoodItemResponse> getAllFoodItems() {
                return food_item_repository.findAll() // gets all food items
                                .stream()
                                .map(food_item_mapper::toResponse) // converts all food items to DTOs
                                .collect(Collectors.toList());

        }

        /**
         * search for food items by name
         */
        @Transactional(readOnly = true)
        public List<FoodItemResponse> searchFoodItemsByName(String name) {
                return food_item_repository.findByName(name)
                                .stream()
                                .map(food_item_mapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * search for food items by tags
         */
        @Transactional(readOnly = true)
        public Set<FoodItemResponse> searchFoodItemsByTagIds(Set<Long> tag_ids) {
                return tag_repository.findByIdIn(tag_ids)
                                .stream()
                                .flatMap(tag -> tag.getFoodItems().stream())
                                .map(food_item_mapper::toResponse)
                                .collect(Collectors.toSet());
        }

        /**
         * returns all available food items
         */
        @Transactional(readOnly = true)
        public List<FoodItemResponse> getAvailableFoodItems() {
                return food_item_repository.findByStatus(FoodItemStatus.AVAILABLE)
                                .stream()
                                .map(food_item_mapper::toResponse)
                                .collect(Collectors.toList());

        }

        /**
         * returns all food items that don't expire before a specified date
         */
        @Transactional(readOnly = true)
        public List<FoodItemResponse> getFoodItemByDate(LocalDate deadline) {
                return food_item_repository.findByExpiryDateLessThanEqual(deadline)
                                .stream()
                                .map(food_item_mapper::toResponse)
                                .collect(Collectors.toList());
        }
}
