package com.communityPantry.communityPantry.mapper;

import com.communityPantry.communityPantry.dto.User.LoginResponse;
import com.communityPantry.communityPantry.domain.UserProfile;

public class UserMapper {

    public static LoginResponse toLoginResponse(UserProfile userProfile, String username, String role, String token) {
        LoginResponse response = new LoginResponse();
        response.setId(userProfile.getId());
        response.setUsername(username);
        response.setSystemRole(role);
        response.setPhoneNumber(userProfile.getPhoneNumber());
        response.setLocation(userProfile.getLocation());
        response.setEmail(userProfile.getEmail());
        response.setToken(token);
        // can later include the users communities and reservations and fooditems if
        // needed later on
        return response;
    }
}