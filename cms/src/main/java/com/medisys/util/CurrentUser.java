//handle db login
package com.medisys.util;

import com.medisys.model.User;

public class CurrentUser {
    private static CurrentUser instance;
    private User currentUser;

    private CurrentUser() {}

    public static synchronized CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
