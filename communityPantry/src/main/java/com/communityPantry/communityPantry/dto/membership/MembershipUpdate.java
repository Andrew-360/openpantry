package com.communityPantry.communityPantry.dto.membership;

import com.communityPantry.communityPantry.domain.enums.CommunityRole;

public class MembershipUpdate {
    private CommunityRole role;
    private Boolean isBanned;

    public CommunityRole getRole() {return role;}
    public void setRole(CommunityRole role) {this.role = role;}
    public Boolean getIsBanned() {return isBanned;}
    public void setIsBanned(Boolean isBanned) {this.isBanned = isBanned;}
}
