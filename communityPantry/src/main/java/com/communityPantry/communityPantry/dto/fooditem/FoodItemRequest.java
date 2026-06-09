package com.communityPantry.communityPantry.dto.fooditem;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTOs are simple, abbreviated forms of entities used when
 * communicating between the frontend (user interface) and backend
 * (servers/databases).
 * It only contains data necessary for interaction.
 * 
 * Frontend sends request DTOs
 * Used by the controller and service (not stored directly in the database)
 */
public class FoodItemRequest {
    // doesn't store reservations; handled by a separate API
    // doesn't store user profile (only ids) for security

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Expiry date is required")
    @FutureOrPresent(message = "Expiry date cannot be in the past")
    private LocalDate expiry_date;

    @NotNull(message = "User profile ID is required")
    private Long user_profile_id;

    @NotNull(message = "Community ID is required")
    private Long community_id;

    private Set<Long> tag_ids;

    private String image;

    /* -------- Getters/Setters -------- */

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImage() {
        return this.image;
    }

     public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiry() {
        return this.expiry_date;
    }

    public void setExpiry(LocalDate expiry_date) {
        this.expiry_date = expiry_date;
    }

    public Long getUserProfileId() {
        return this.user_profile_id;
    }

    public void setUserProfileId(Long user_profile_id) {
        this.user_profile_id = user_profile_id;
    }

    public Long getCommunityId() {
        return this.community_id;
    }

    public void setCommunityId(Long community_id) {
        this.community_id = community_id;
    }

    public Set<Long> getTagIds() {
        return this.tag_ids;
    }

    public void setTagIds(Set<Long> tag_ids) {
        this.tag_ids = tag_ids;
    }
}
