package com.communityPantry.communityPantry.dto.fooditem;

import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTOs are simple, abbreviated containers that represent entities used when
 * communicating between the frontend (user interface) and backend
 * (server/database).
 * It only contains data necessary for interaction.
 * 
 * Backend returns Response DTOs
 * Used by the controller and service (not stored directly in the database)
 */
public class FoodItemResponse {

    // doesn't store reservations; handled by separate API
    private Long id;
    private String name;
    private String description;
    private Integer quantity;
    private LocalDate expiry_date;
    private FoodItemStatus status;
    private Long user_profile_id;
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

    public FoodItemStatus getStatus() {
        return this.status;
    }

    public void setStatus(FoodItemStatus status) {
        this.status = status;
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

    public void setTagIds(Set<Long> tags) {
        this.tag_ids = tags;
    }

    public String getImage() {
        return this.image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
}
