package br.com.user_service.enums;

public enum Role {
    ADMIN(5),
    USER(2),
    VIEWER(1),
    EDITOR(2);

    private final long expirationMinutes;

    Role(long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }
}