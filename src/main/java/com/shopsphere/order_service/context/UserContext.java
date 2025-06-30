package com.shopsphere.order_service.context;

public final class UserContext {

    private static final ThreadLocal<String> currentUserContext = new ThreadLocal<>();

    public static void set(final String userId) {
        currentUserContext.set(userId);
    }

    public static String get() {
        return currentUserContext.get();
    }

    public static void clear() {
        currentUserContext.remove();
    }

    private UserContext() {
    }
}
