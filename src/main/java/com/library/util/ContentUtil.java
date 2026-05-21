package com.library.util;

public final class ContentUtil {
    private ContentUtil() {}

    /** Plain-text preview for cards (strips HTML and limits length). */
    public static String toPreview(String html, int maxLen) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String text = html
                .replaceAll("(?s)<style.*?>.*?</style>", " ")
                .replaceAll("(?s)<script.*?>.*?</script>", " ")
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen) + "...";
    }
}
