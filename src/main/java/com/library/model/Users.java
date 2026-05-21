package com.library.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Users {
    public static final int ROLE_USER = 0;
    public static final int ROLE_ADMIN = 1;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_SUSPENDED = 0;
    public static final int MAX_FAILED_LOGINS = 5;

    private int id;
    private String username;
    private String email;
    private String password;
    private int role;
    private String avatarPath;
    private String bio;
    private int accountStatus = STATUS_ACTIVE;
    private int failedLoginAttempts;

    public boolean isAdmin() {
        return role == ROLE_ADMIN;
    }

    public boolean isSuspended() {
        return accountStatus == STATUS_SUSPENDED;
    }

    public String getRoleLabel() {
        return role == ROLE_ADMIN ? "Admin" : "User";
    }
}
