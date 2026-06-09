package com.communityPantry.communityPantry.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;

/**
 * Represents a food listing posted by a user in a community pantry.
 * Each food item can have multiple tags and reservations.
 */

@Entity // Database table object
@Table(name = "food_items") // defines the table name
public class FoodItem {
    /**
     * creates a new entity FoodItem
     * with fields id, name, description, quantity, expiry date, status
     */

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment id
    @Column(name = "id")
    private Long id; // unique identification of the food item

    @Column(nullable = false) // database column is required
    private String name; // name of the food item

    private String description; // optional description

    @Column(nullable = false)
    private Integer quantity; // number of portions available

    @Column(nullable = false)
    private LocalDate expiryDate; // date food expires

    @Column(name = "image")
    private String image; // optional image URL

    // an enum for the status of the food item (AVAILABLE, RESERVED, EXPIRED)
    @Enumerated(EnumType.STRING) // stores enum text
    @Column(nullable = false)
    private FoodItemStatus status;

    /*--------- Relationships --------- */

    /**
     * FOOD ITEM → USER PROFILE
     * many food items can be posted by one user profile
     */
    @ManyToOne // many items belong to one parent
    @JoinColumn(name = "userProfile") // foreign key column
    private UserProfile userProfile;

    /**
     * FOOD ITEM → COMMUNITY
     * many food items can belong to one community
     */
    @ManyToOne
    @JoinColumn(name = "community") // foreign key column
    private Community community;

    /**
     * FOOD ITEM ↔ TAG
     * Many-to-many relationship for hashtag-style tags
     */
    @ManyToMany // many-to-many relationship
    @JoinTable(name = "food_item_tags", joinColumns = @JoinColumn(name = "food_item_id"), // foreign key column
            inverseJoinColumns = @JoinColumn(name = "tags") // foreign key column
    )
    // using hashsets ensures no duplicates and fast lookups
    private Set<Tag> tags = new HashSet<>();

    /**
     * FOOD ITEM → RESERVATIONS
     * one food item can have many reservations
     */
    @OneToMany(mappedBy = "foodItem") // one parent has many children
    // using sets ensures no duplications
    private Set<Reservation> reservations = new HashSet<>();

    // custom constructors are optional
    public FoodItem() {
    }

    /*--------- Getters --------- */

    // retrieves the unique id of the food item
    public Long getId() {
        return this.id;
    }

    // retrieves the name of the food item
    public String getName() {
        return this.name;
    }

    // retrieves the description of the food item
    public String getDescription() {
        return this.description;
    }

    // retrieves the quantity of the food item
    public Integer getQuantity() {
        return this.quantity;
    }

    // retrieves the expiry date of the food item
    public LocalDate getExpiry() {
        return this.expiryDate;
    }

    // retrieves the status of the food item
    public FoodItemStatus getStatus() {
        return this.status;
    }

    // retrieves the user profile attached to this food item
    public UserProfile getUserProfile() {
        return userProfile;
    }

    // retrieves the community the food item was posted to
    public Community getCommunity() {
        return community;
    }

    // retrieves the tags associated with this food item
    public Set<Tag> getTags() {
        return tags;
    }

    // retrieves reservations that have been made on this food item
    public Set<Reservation> getReservations() {
        return reservations;
    }

    // retrieves the image URL of the food item
    public String getImage() {
        return image;
    }

    /*--------- Setters --------- */

    // sets the id of the food item
    public void setId(Long id) {
        this.id = id;
    }

    // sets the name of the food item
    public void setName(String name) {
        this.name = name;
    }

    // sets the description of the food item
    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setExpiry(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setStatus(FoodItemStatus status) {
        this.status = status;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void setImage(String image) {
        this.image = image;
    }

    /*--------- Helpers --------- 
    // checks if the food item has expired
    public Boolean isExpired(){}
    
    // checks if the amount to be reserved is valid
    public Boolean validReservation(Integer amount_reserved){
        return (this.quantity >= amount_reserved);
    }
    // updates the portions available after a reservation has been made
    public void reserveQuantity(Integer amount_reserved){
        this.quantity = this.quantity - amount_reserved;
    }
    
    // checks if a specific allergen is present in the food item
    public Boolean checkAllergen(String allergen){
    
    }*/
}
