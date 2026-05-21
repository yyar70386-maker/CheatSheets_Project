package com.library.util;

import com.library.model.Users;

public final class AuthenticationResult {
    public enum Status {
        SUCCESS,
        NOT_FOUND,
        INVALID_PASSWORD,
        SUSPENDED,
        LOCKED
    }

    private final Status status;
    private final Users user;
    private final int attemptsRemaining;

    private AuthenticationResult(Status status, Users user, int attemptsRemaining) {
        this.status = status;
        this.user = user;
        this.attemptsRemaining = attemptsRemaining;
    }

    public static AuthenticationResult success(Users user) {
        return new AuthenticationResult(Status.SUCCESS, user, 0);
    }

    public static AuthenticationResult notFound() {
        return new AuthenticationResult(Status.NOT_FOUND, null, 0);
    }

    public static AuthenticationResult invalidPassword(int attemptsRemaining) {
        return new AuthenticationResult(Status.INVALID_PASSWORD, null, attemptsRemaining);
    }

    public static AuthenticationResult suspended() {
        return new AuthenticationResult(Status.SUSPENDED, null, 0);
    }

    public static AuthenticationResult locked() {
        return new AuthenticationResult(Status.LOCKED, null, 0);
    }

    public Status getStatus() {
        return status;
    }

    public Users getUser() {
        return user;
    }

    public int getAttemptsRemaining() {
        return attemptsRemaining;
    }
}
