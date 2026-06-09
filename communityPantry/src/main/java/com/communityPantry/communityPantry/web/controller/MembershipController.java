package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.dto.membership.MembershipResponse;
import com.communityPantry.communityPantry.service.MembershipService;
import com.communityPantry.communityPantry.web.interfaces.MembershipControllerInterface;
import com.communityPantry.communityPantry.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController implements MembershipControllerInterface {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    // Create a new membership for the current user
    @Override
    @PostMapping("/community/{communityId}")
    public ResponseEntity<MembershipResponse> createMembership(Authentication authentication, @PathVariable Long communityId) {
        Membership created = membershipService.createMembership(communityId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MembershipResponse(created));
    }

    // Update a current membership.
    // Give community and user profile in id
    // Supply new isBanned and/or role in body
    // Other variables should not be editable
    @Override
    @PatchMapping("/community/{communityId}/user/{userProfileId}")
    public ResponseEntity<MembershipResponse> updateMembership(
            Authentication authentication,
            @PathVariable Long communityId,
            @PathVariable Long userProfileId,
            @RequestBody Map<String, Object> membershipDetails) {

        Membership updatedMembership = membershipService.updateMembership(authentication, communityId, userProfileId, membershipDetails);
        return ResponseEntity.ok(new MembershipResponse(updatedMembership));
    }

    // Delete a membership
    @Override
    @DeleteMapping("/community/{communityId}/user/{userProfileId}")
    public ResponseEntity<String> deleteMembership(Authentication authentication, @PathVariable Long communityId,
            @PathVariable Long userProfileId) {
        membershipService.deleteMembership(authentication, communityId, userProfileId);
        return ResponseEntity.ok("success");
    }

    @Override
    @DeleteMapping("/community/{communityId}")
    public ResponseEntity<String> leaveCommunity(Authentication authentication, @PathVariable Long communityId){
        membershipService.leaveCommunity(authentication, communityId);
        return ResponseEntity.ok("success");
    }


    // Get all memberships for a community
    @Override
    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<MembershipResponse>> getMembershipsByCommunity(@PathVariable Long communityId) {
        return ResponseEntity.ok(
                membershipService.getMembershipsByCommunity(communityId).stream()
                        .map(MembershipResponse::new)
                        .toList()
        );
    }

    // Get all memberships for a user
    @Override
    @GetMapping("/user/{userProfileId}")
    public ResponseEntity<List<MembershipResponse>> getMembershipsByUserProfile(@PathVariable Long userProfileId) {
        return ResponseEntity.ok(
                membershipService.getMembershipsByUserProfile(userProfileId).stream()
                        .map(MembershipResponse::new)
                        .toList()
        );
    }

    // Get a membership for a given community and userprofile
    // Use to check if a user is a member of a community and what role they have
    @Override
    @GetMapping("/community/{communityId}/user/{userProfileId}")
    public ResponseEntity<MembershipResponse> getMembershipByCommunityAndUserProfile(@PathVariable Long communityId,
            @PathVariable Long userProfileId) {
        Membership mem = membershipService.getMembershipByCommunityAndUserProfile(communityId, userProfileId)
                .orElseThrow(() -> new EntityNotFoundException("Membership not found for communityId: " + communityId
                + " and userProfileId: " + userProfileId));
        return ResponseEntity.ok(new MembershipResponse(mem));
    }
}
