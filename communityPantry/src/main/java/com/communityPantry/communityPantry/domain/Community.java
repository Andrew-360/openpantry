package com.communityPantry.communityPantry.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "communities")

public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Fields

    @Column(nullable = false, unique = true, name = "name")
    private String name;

    @Column(nullable = false, name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    // Relationships

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Membership> memberships = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community")
    private Set<FoodItem> foodItems = new HashSet<>();

    // Constructors, getters, and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<Membership> memberships) {
        if (this.memberships != null) {
            this.memberships.forEach(membership -> membership.setCommunity(null));
        }

        if (memberships != null) {
            memberships.forEach(membership -> membership.setCommunity(this));
        }

        this.memberships = memberships;
    }

    public void addMembership(Membership membership) {
        memberships.add(membership);
        membership.setCommunity(this);
    }

    public void removeMembership(Membership membership) {
        memberships.remove(membership);
        membership.setCommunity(null);
    }

    public Set<FoodItem> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(Set<FoodItem> foodItems) {
        if (this.foodItems != null) {
            this.foodItems.forEach(foodItem -> foodItem.setCommunity(null));
        }

        if (foodItems != null) {
            foodItems.forEach(foodItem -> foodItem.setCommunity(this));
        }

        this.foodItems = foodItems;
    }

    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
        foodItem.setCommunity(this);
    }

    public void removeFoodItem(FoodItem foodItem) {
        foodItems.remove(foodItem);
        foodItem.setCommunity(null);
    }

    @Override
    public String toString() {
        return "Community{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Community))
            return false;
        return getId() != null && getId().equals(((Community) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}