package com.communityPantry.communityPantry.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import com.communityPantry.communityPantry.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // this class implements the UserDetailsService interface from Spring Security
    // so that user details can be loaded from the database for authentication and
    // authorization
    // i.e. its the link between the User entity and Spring Security, it tells
    // Spring Security how to load user details from the database

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
