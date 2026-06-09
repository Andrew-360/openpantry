package com.communityPantry.communityPantry.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import java.util.Set;
import java.util.HashSet;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "user_profiles")

public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // relationship

    // saying that this user profile is assocaiated with one user and the user id
    // and user profile id are the same (its a primary key and a foreign key)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // one to many relationship with food items
    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY)
    private Set<FoodItem> foodItems = new HashSet<>();

    // one to many relationship with reservations
    @OneToMany(mappedBy = "userProfile", fetch = FetchType.LAZY)
    private Set<Reservation> reservations = new HashSet<>();

    // user profile - * membersips
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Membership> memberships = new HashSet<>();

    //

    // fields

    // additional fields we want to add to the user profile
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotNull
    @Column(name = "location", nullable = false)
    private String location;

    @NotNull
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    // as we are using @MapsId, we don't need to set the id manually as it will be
    // automatically set to the same value as the user id when we save the user
    // profile. So we can remove the setId method.
    // BUT is used for the unit tests
    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // relationship getters and setters

    public Set<Membership> getMemberships() {
        return this.memberships;
    }

    // dont really use this use add and remove helper functions instead
    public void setMemberships(Set<Membership> memberships) {
        this.memberships = memberships;
    }

    public void addMembership(Membership membership) {
        this.memberships.add(membership);
        membership.setUserProfile(this);
    }

    public void removeMembership(Membership membership) {
        this.memberships.remove(membership);
        membership.setUserProfile(null);
    }

    public Set<FoodItem> getFoodItems() {
        return this.foodItems;
    }

    // need to do the bidirectional relationship maintenance for food items
    public void setFoodItems(Set<FoodItem> foodItems) {
        if (this.foodItems != null) {
            this.foodItems.forEach(fi -> fi.setUserProfile(null));
        }
        if (foodItems != null) {
            foodItems.forEach(fi -> fi.setUserProfile(this));
        }
        this.foodItems = foodItems;
    }

    public void addFoodItem(FoodItem foodItem) {
        this.foodItems.add(foodItem);
        foodItem.setUserProfile(this);
    }

    public void removeFoodItem(FoodItem foodItem) {
        this.foodItems.remove(foodItem);
        foodItem.setUserProfile(null);
    }

    public Set<Reservation> getReservations() {
        return this.reservations;
    }

    // same as food items, we need to maintain the bidirectional relationship
    public void setReservations(Set<Reservation> reservations) {
        if (this.reservations != null) {
            this.reservations.forEach(r -> r.setUserProfile(null));
        }
        if (reservations != null) {
            reservations.forEach(r -> r.setUserProfile(this));
        }
        this.reservations = reservations;
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
        reservation.setUserProfile(this);
    }

    public void removeReservation(Reservation reservation) {
        this.reservations.remove(reservation);
        reservation.setUserProfile(null);
    }

    // to string, equals and hashcode
    // we can use the id for equals and hashcode as it is unique for each user
    // profiles
    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserProfile))
            return false;
        return getId() != null && getId().equals(((UserProfile) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
