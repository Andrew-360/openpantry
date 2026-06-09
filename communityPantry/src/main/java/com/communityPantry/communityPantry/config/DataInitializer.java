package com.communityPantry.communityPantry.config;

//import the domain and the repositories
import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.FoodItem;
import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.domain.Reservation;
import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.enums.CommunityRole;
import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;
import com.communityPantry.communityPantry.domain.enums.SystemRole;
import com.communityPantry.communityPantry.domain.enums.ReservationStatus;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.FoodItemRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.repository.UserRepository;
import com.communityPantry.communityPantry.repository.MembershipRepository;
import com.communityPantry.communityPantry.repository.ReservationRepository;
import com.communityPantry.communityPantry.repository.TagRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

@Component
@ConditionalOnProperty(name = "app.seed-data.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    // the repositiories to save to the database
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CommunityRepository communityRepository;
    private final MembershipRepository membershipRepository;
    private final TagRepository tagRepository;
    private final FoodItemRepository foodItemRepository;
    private final ReservationRepository reservationRepository;

    public DataInitializer(UserRepository userRepository, UserProfileRepository userProfileRepository,
                           CommunityRepository communityRepository, MembershipRepository membershipRepository,
                           TagRepository tagRepository, FoodItemRepository foodItemRepository,
                           ReservationRepository reservationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
        this.tagRepository = tagRepository;
        this.foodItemRepository = foodItemRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        User admin1 = createUser("adminEin", "adminPassword", SystemRole.ADMIN);
        User user1 = createUser("userEin", "userPassword", SystemRole.USER);


        Community comm1 = createCommunity("Nu community", "A nu community for food donations", "Selly Oak");
        Community comm2 = createCommunity("Mew community", "A mew community for food donations", "Bournbrook");
        Community comm3 = createCommunity("Gamma community", "A gamma community for food donations", "Edgbaston");

        UserProfile adminProfile1 = createUserProfile(admin1, "Selly oak", "adminEin@example.com", "098321");
        UserProfile userProfile1 =createUserProfile(user1, "Selly oak", "userEin@example.com", "09212");

        Membership membership1 = createMembership(comm1, adminProfile1, CommunityRole.MODERATOR);
        Membership membership2 = createMembership(comm1, userProfile1, CommunityRole.MEMBER);

        Tag tag1 = createTag("Gluten-Free");
        Tag tag2 = createTag("Vegan");
        Tag tag3 = createTag("Vegetarian");
        Tag tag4 = createTag("Halal");

        FoodItem pizza = createFoodItem("Pizza", "Amazing Pizza as good as Big Johns :)", 10, FoodItemStatus.AVAILABLE, userProfile1, comm1, LocalDate.of(2026,10,24), tag1, tag2);
        FoodItem pasta = createFoodItem("Vegan Pasta", "Excellent Pasta you should try this", 8, FoodItemStatus.AVAILABLE, userProfile1, comm2, LocalDate.of(2026,10,24),tag2);
        FoodItem curry = createFoodItem("Curry", "Great curry", 0, FoodItemStatus.RESERVED, userProfile1, comm3, LocalDate.of(2026,10,24),tag3);
        FoodItem chicken = createFoodItem("Chicken", "Delicious chicken that has sadly expired :(", 1, FoodItemStatus.EXPIRED, userProfile1, comm3, LocalDate.of(2026,1,24), tag4);

        Reservation reservation1 = createReservation(pizza, adminProfile1, 2, ReservationStatus.PENDING);
        Reservation reservation2 = createReservation(curry, adminProfile1, 1, ReservationStatus.COMPLETED);
        Reservation reservation3 = createReservation(chicken, adminProfile1, 1, ReservationStatus.CANCELLED);
    }

    private User createUser(String username, String password, SystemRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
        return user;
    }
    private UserProfile createUserProfile(User user, String location, String email, String phoneNumber) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setLocation(location);
        userProfile.setEmail(email);
        userProfile.setPhoneNumber(phoneNumber);
        userProfileRepository.save(userProfile);
        return userProfile;
    }
    private Community createCommunity(String name, String description, String location) {
        Community community = new Community();
        community.setName(name);
        community.setDescription(description);
        community.setLocation(location);
        communityRepository.save(community);
        return community;
    }
    private Membership createMembership(Community community, UserProfile userProfile, CommunityRole role) {
        Membership membership = new Membership();
        membership.setCommunity(community);
        membership.setUserProfile(userProfile);
        membership.setRole(role);
        membership.setJoinDate(LocalDate.now());
        membership.setIsBanned(false);
        membershipRepository.save(membership);
        return membership;
    }
    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagRepository.save(tag);
        return tag;
    }
    private FoodItem createFoodItem (String name, String description, int quantity, FoodItemStatus status, UserProfile userProfile, Community community, LocalDate expiryDate, Tag... tags) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(name);
        foodItem.setDescription(description);
        foodItem.setQuantity(quantity);
        foodItem.setStatus(status);
        foodItem.setExpiry(expiryDate);
        foodItem.setUserProfile(userProfile);
        foodItem.setCommunity(community);
        foodItem.setTags(new HashSet<>(Set.of(tags)));
        foodItemRepository.save(foodItem);
        return foodItem;
    }
    private Reservation createReservation(FoodItem foodItem, UserProfile userProfile, int quantity, ReservationStatus status) {
        Reservation reservation = new Reservation();
        reservation.setFoodItem(foodItem);
        reservation.setUserProfile(userProfile);
        reservation.setQuantity(quantity);
        reservation.setReservationStatus(status);
        reservation.setReservationDate(LocalDate.now());
        reservationRepository.save(reservation);
        return reservation;
    }

}
