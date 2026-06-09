package com.communityPantry.communityPantry.dto.membership;

import com.communityPantry.communityPantry.domain.Membership;

public class MembershipResponse {
    public long membershipId;
    public String role;
    public boolean isBanned;
    public String joinDate;

    public String communityName;
    public Long communityId;

    public String username;
    public Long userProfileId;

    public MembershipResponse(Membership membership) {
        this.membershipId = membership.getId();
        this.role = membership.getRole().name();
        this.isBanned = membership.getIsBanned();
        this.joinDate = membership.getJoinDate().toString();

        if (membership.getCommunity() != null) {
            this.communityName = membership.getCommunity().getName();
            this.communityId = membership.getCommunity().getId();
        }

        if (membership.getUserProfile() != null && membership.getUserProfile().getUser() != null) {
            this.username = membership.getUserProfile().getUser().getUsername();
            this.userProfileId = membership.getUserProfile().getId();
        }
    }
}
