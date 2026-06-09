package com.communityPantry.communityPantry.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;
import com.communityPantry.communityPantry.domain.FoodItem;

/**
 * Handles database access to food items
 */

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    /*--------- Lookups --------- */

    FoodItem findFoodItemById(Long id);

    // lists all food items
    List<FoodItem> findAll();

    // lists food items of a specified status
    List<FoodItem> findByStatus(FoodItemStatus status);

    // lists food items containing a specified name
    List<FoodItem> findByName(String name);

    // lists food items that don't expire before the specified date
    List<FoodItem> findByExpiryDateLessThanEqual(LocalDate deadline);

}
