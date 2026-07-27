package de.drpacket.jumpplugin.navigator;

public enum NavigatorAnimation {
    NONE,
    ROWS,
    RANDOM;

    public static NavigatorAnimation fromConfig(String value) {
        if (value == null) {
            return NONE;
        }
        try {
            return NavigatorAnimation.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return NONE;
        }
    }
}
