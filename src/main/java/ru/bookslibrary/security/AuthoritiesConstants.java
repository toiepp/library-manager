package ru.bookslibrary.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String EMPLOYEE = "ROLE_EMPLOYEE";

    public static final String GUEST = "ROLE_GUEST";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
