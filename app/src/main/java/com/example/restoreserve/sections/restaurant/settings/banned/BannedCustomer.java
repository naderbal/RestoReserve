package com.example.restoreserve.sections.restaurant.settings.banned;

import com.example.restoreserve.data.user.User;

/**
 *
 */

public class BannedCustomer {
    User user;
    boolean isBanned;

    public BannedCustomer(User user, boolean isBanned) {
        this.user = user;
        this.isBanned = isBanned;
    }

    public User getUser() {
        return user;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }
}
