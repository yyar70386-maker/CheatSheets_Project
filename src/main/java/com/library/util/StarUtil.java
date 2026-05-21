package com.library.util;

public final class StarUtil {
    private StarUtil() {}

    public static int toStarCount(double average) {
        if (average <= 0) {
            return 0;
        }
        return Math.max(1, Math.min(5, (int) Math.round(average)));
    }

    public static String renderStars(double average) {
        int filled = toStarCount(average);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= filled) {
                sb.append("<i class=\"fa-solid fa-star star-filled\"></i>");
            } else {
                sb.append("<i class=\"fa-regular fa-star star-empty\"></i>");
            }
        }
        return sb.toString();
    }
}
