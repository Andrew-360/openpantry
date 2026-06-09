    package com.communityPantry.communityPantry.domain;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Table;
    import jakarta.validation.constraints.NotNull;
    import jakarta.persistence.Id;

    import java.util.HashSet;
    import java.util.Objects;
    import java.util.Set;

    @Entity
    @Table(name = "tags")

    public class Tag {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        // Relationships

        @JsonIgnore
        @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
        private Set<FoodItem> foodItems = new HashSet<>();

        // Fields

        @NotNull
        @Column(nullable = false, unique = true, name = "name")
        private String name;

        // Constructors

        public Tag() {
        }

        public Tag(String name) {
            this.name = name;
        }

        // Getters and Setters

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<FoodItem> getFoodItems() {
            return foodItems;
        }

        public void setFoodItems(Set<FoodItem> foodItems) {
            this.foodItems = foodItems;
        }

        // Other methods

        @Override
        public String toString() {
            return "Tag{" + "id=" + id + ", name=" + name + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            return Objects.equals(id, ((Tag) o).id);
        }

        @Override
        public int hashCode() {
            return Long.hashCode(id);
        }
    }